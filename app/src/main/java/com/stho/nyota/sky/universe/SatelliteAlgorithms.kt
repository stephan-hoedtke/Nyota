package com.stho.nyota.sky.universe

import com.stho.nyota.sky.universe.Algorithms.ThetaG
import com.stho.nyota.sky.utilities.Vector

/**
 * Created by shoedtke on 20.01.2017.
 * References:
 * (1) SDG, SGP4, SDP4, SGP8, SDP8 in pascal, Dominik Brodowski, 2006:
 * https://www.brodo.de/space/sgp/pascal.html
 * (2) Documentation: Dr. T.S. Kelso, 2014:
 * http://www.celestrak.com/NORAD/documentation/
 * (3) The Algorithm + Fortran Code by Kelso:
 * https://celestrak.com/NORAD/documentation/spacetrk.pdf
 */
object SatelliteAlgorithms {
    private const val SXPX_ERR_NEARLY_PARABOLIC = -1
    private const val SXPX_ERR_NEGATIVE_MAJOR_AXIS = -2
    private const val SXPX_WARN_ORBIT_WITHIN_EARTH = -3
    private const val SXPX_WARN_PERIGEE_WITHIN_EARTH = -4
    private const val PI = 3.141592653589793238462643383279502884197
    private const val PT2 = 6.28318530718
    private const val twopi = PI * 2.0
    private const val minus_xj3 = 2.53881E-6
    private const val MINUTES_PER_DAY = 1440.0
    private const val earth_radius_in_km = 6378.135
    private const val e6a = 1.0E-6
    private const val two_thirds = 2.0 / 3.0
    private const val ae = 1.0
    private const val xj2 = 1.082616e-3
    private const val ck2 = 0.5 * xj2 * ae * ae
    private const val EARTH_MAJOR_AXIS_IN_METERS = 6378140.0
    private const val EARTH_MINOR_AXIS_IN_METERS = 6356755.0
    private const val EARTH_AXIS_RATIO = EARTH_MINOR_AXIS_IN_METERS / EARTH_MAJOR_AXIS_IN_METERS
    private const val xj4 = -1.65597e-6
    private const val ck4 = -0.375 * xj4 * ae * ae * ae * ae
    private const val s = ae * (1.0 + 78.0 / earth_radius_in_km)
    private const val qoms2t = 1.880279159015270643865e-9
    private const val xke = 0.0743669161331734132
    private const val a3ovk2 = minus_xj3 / ck2 * ae * ae * ae

    fun calculatePositionVelocity(tle: TLE, julianDay: Double, position: Vector, velocity: Vector?) {
        val parameters = SatelliteParameters()
        SDP8_init(tle, parameters)
        val minutes = GetMinutesSinceTLE(tle, julianDay)
        SDP8(minutes, tle, parameters, position, velocity)
    }

    private fun GetMinutesSinceTLE(tle: TLE, julianDay: Double): Double {
        return MINUTES_PER_DAY * (julianDay - tle.Epoch)
    }

    private fun SDP8_init(tle: TLE, Params: SatelliteParameters) {
        val rho = 0.15696615
        val b = tle.bstar * 2.0 / rho
        val alpha2: Double
        val b1: Double
        val b2: Double
        val b3: Double
        val c0: Double
        val c1: Double
        val c4: Double
        val c5: Double
        val cos2g: Double
        val d1: Double
        val d2: Double
        val d3: Double
        val d4: Double
        val d5: Double
        val eeta: Double
        val eta: Double
        val eta2: Double
        val po: Double
        val psim2: Double
        var r1: Double
        val tsi: Double
        val xndtn: Double
        Utilities.CommonInitialization(tle, Params)
        sxp8_common_init(tle, Params)
        Params.sinio = Math.sin(tle.xincl)
        po = Params.aodp * Params.betao2
        tsi = 1.0 / (po - s)
        eta = tle.eo * s * tsi
        eta2 = eta * eta
        r1 = 1.0 / (1.0 - eta2)
        psim2 = Math.abs(r1)
        alpha2 = Params.eosq + 1.0
        eeta = tle.eo * eta
        cos2g = Params.cosg * Params.cosg * 2.0 - 1.0
        d5 = tsi * psim2
        d1 = d5 / po
        d2 = eta2 * (eta2 * 4.5 + 36.0) + 12.0
        d3 = eta2 * (eta2 * 2.5 + 15.0)
        d4 = eta * (eta2 * 3.75 + 5.0)
        b1 = ck2 * Params.tthmun
        b2 = -ck2 * Params.unmth2
        b3 = a3ovk2 * Params.sinio
        r1 = tsi
        r1 *= r1
        c0 = b * .5 * rho * qoms2t * Params.xnodp * Params.aodp *
                (r1 * r1) * Math.pow(psim2, 3.5) / Math.sqrt(alpha2)
        r1 = alpha2
        c1 = Params.xnodp * 1.5 * (r1 * r1) * c0
        c4 = d1 * d3 * b2
        c5 = d5 * d4 * b3
        Params.xndt = c1 * (eta2 * (Params.eosq * 34.0 + 3.0) + 2.0 + eeta * 5.0 * (eta2 + 4.0) + Params.eosq * 8.5 + d1 * d2 * b1 + c4 * cos2g + c5 * Params.sing)
        xndtn = Params.xndt / Params.xnodp
        Params.edot = -two_thirds * xndtn * (1.0 - tle.eo)
        Deep_dpinit(tle, Params)
    }

    private fun SDP8(tsince: Double, tle: TLE, Params: SatelliteParameters, position: Vector, velocity: Vector?) {
        val am: Double
        val aovr: Double
        val axnm: Double
        val aynm: Double
        val beta: Double
        val beta2m: Double
        var cose: Double
        val cosos: Double
        val cs2f2g: Double
        val csf: Double
        val csfg: Double
        val cslamb: Double
        val di: Double
        val diwc: Double
        val dr: Double
        val ecosf: Double
        var fm: Double
        val g1: Double
        val g10: Double
        val g13: Double
        val g14: Double
        val g2: Double
        val g3: Double
        val g4: Double
        val g5: Double
        val pm: Double
        var r1: Double
        val rdot: Double
        val rm: Double
        val rr: Double
        val rvdot: Double
        var sine: Double
        val sinos: Double
        val sn2f2g: Double
        val snf: Double
        val snfg: Double
        val sni2du: Double
        val sinio2: Double
        val snlamb: Double
        var temp: Double
        val ux: Double
        val uy: Double
        val uz: Double
        val vx: Double
        val vy: Double
        val vz: Double
        val xlamb: Double
        var xmam: Double
        var xmamdf: Double
        val y4: Double
        val y5: Double
        val z1: Double
        val z7: Double
        var zc2: Double
        var zc5: Double
        var i: Int
        z1 = Params.xndt * 0.5 * tsince * tsince
        z7 = two_thirds * 3.5 * z1 / Params.xnodp
        xmamdf = tle.xmo + Params.xmdot * tsince
        Params.omgadf = tle.omegao + Params.omgdot * tsince + z7 * Params.xgdt1
        Params.xnode = tle.xnodeo + Params.xnodot * tsince + z7 * Params.xhdt1
        Params.xn = Params.xnodp
        Params.xll = xmamdf
        Params.t = tsince
        Utilities.Deep_dpsec(tle, Params)
        xmamdf = Params.xll
        Params.xn += Params.xndt * tsince
        Params.em += Params.edot * tsince
        xmam = xmamdf + z1 + z7 * Params.xmdt1
        Params.xll = xmam
        Utilities.Deep_dpper(tle, Params)
        xmam = Params.xll
        xmam = Utilities.FMod2p(xmam)
        zc2 = xmam + Params.em * Math.sin(xmam) * (Params.em * Math.cos(xmam) + 1.0)
        i = 0
        do {
            var cape: Double
            sine = Math.sin(zc2)
            cose = Math.cos(zc2)
            zc5 = 1.0 / (1.0 - Params.em * cose)
            cape = (xmam + Params.em * sine - zc2) * zc5 + zc2
            r1 = cape - zc2
            if (Math.abs(r1) <= e6a) break
            zc2 = cape
        } while (i++ < 10)
        am = Math.pow(xke / Params.xn, two_thirds)
        beta2m = 1.0 - Params.em * Params.em
        sinos = Math.sin(Params.omgadf)
        cosos = Math.cos(Params.omgadf)
        axnm = Params.em * cosos
        aynm = Params.em * sinos
        pm = am * beta2m
        g1 = 1.0 / pm
        g2 = ck2 * .5 * g1
        g3 = g2 * g1
        beta = Math.sqrt(beta2m)
        g4 = a3ovk2 * .25 * Params.sinio
        g5 = a3ovk2 * .25 * g1
        snf = beta * sine * zc5
        csf = (cose - Params.em) * zc5
        fm = Math.atan2(snf, csf)
        if (fm < 0.0) fm += PI + PI
        snfg = snf * cosos + csf * sinos
        csfg = csf * cosos - snf * sinos
        sn2f2g = snfg * 2.0 * csfg
        r1 = csfg
        cs2f2g = r1 * r1 * 2.0 - 1.0
        ecosf = Params.em * csf
        g10 = fm - xmam + Params.em * snf
        rm = pm / (ecosf + 1.0)
        aovr = am / rm
        g13 = Params.xn * aovr
        g14 = -g13 * aovr
        dr = g2 * (Params.unmth2 * cs2f2g - Params.tthmun * 3.0) - g4 * snfg
        diwc = g3 * 3.0 * Params.sinio * cs2f2g - g5 * aynm
        di = diwc * Params.cosio
        sinio2 = Math.sin(Params.xinc * .5)
        sni2du = Params.sini2 * (g3 * ((1.0 - Params.theta2 * 7.0) * .5 * sn2f2g - Params.unm5th *
                3.0 * g10) - g5 * Params.sinio * csfg * (ecosf + 2.0)) - g5 * .5 *
                Params.theta2 * axnm / Params.cosi2
        xlamb = fm + Params.omgadf + Params.xnode + g3 * ((Params.cosio * 6.0 +
                1.0 - Params.theta2 * 7.0) * .5 * sn2f2g - (Params.unm5th + Params.cosio * 2.0) *
                3.0 * g10) + g5 * Params.sinio * (Params.cosio * axnm /
                (Params.cosio + 1.0) - (ecosf + 2.0) * csfg)
        y4 = sinio2 * snfg + csfg * sni2du + snfg * .5 * Params.cosi2 * di
        y5 = sinio2 * csfg - snfg * sni2du + csfg * .5 * Params.cosi2 * di
        rr = rm + dr
        rdot = Params.xn * am * Params.em * snf / beta + g14 * (g2 * 2.0 * Params.unmth2 * sn2f2g + g4 * csfg)
        r1 = am
        rvdot = Params.xn * (r1 * r1) * beta / rm + g14 * dr + am * g13 * Params.sinio * diwc
        snlamb = Math.sin(xlamb)
        cslamb = Math.cos(xlamb)
        temp = (y5 * snlamb - y4 * cslamb) * 2.0
        ux = y4 * temp + cslamb
        vx = y5 * temp - snlamb
        temp = (y5 * cslamb + y4 * snlamb) * 2.0
        uy = -y4 * temp + snlamb
        vy = -y5 * temp + cslamb
        temp = Math.sqrt(1.0 - y4 * y4 - y5 * y5) * 2.0
        uz = y4 * temp
        vz = y5 * temp
        position.x = rr * ux * earth_radius_in_km
        position.y = rr * uy * earth_radius_in_km
        position.z = rr * uz * earth_radius_in_km
        if (velocity != null) {
            velocity.x = (rdot * ux + rvdot * vx) * earth_radius_in_km
            velocity.y = (rdot * uy + rvdot * vy) * earth_radius_in_km
            velocity.z = (rdot * uz + rvdot * vz) * earth_radius_in_km
        }
    }

    private fun sxp8_common_init(tle: TLE, Params: SatelliteParameters) {
        val half_inclination = tle.xincl * 0.5
        val theta4 = Params.theta2 * Params.theta2
        val po: Double
        val pom2: Double
        val pardt1: Double
        val pardt2: Double
        val pardt4: Double
        Params.sing = Math.sin(tle.omegao)
        Params.cosg = Math.cos(tle.omegao)
        Params.sini2 = Math.sin(half_inclination)
        Params.cosi2 = Math.cos(half_inclination)
        Params.tthmun = Params.theta2 * 3.0 - 1.0
        Params.unm5th = 1.0 - Params.theta2 * 5.0
        Params.unmth2 = 1.0 - Params.theta2
        po = Params.aodp * Params.betao2
        pom2 = 1.0 / (po * po)
        pardt1 = 3.0 * ck2 * pom2 * Params.xnodp
        pardt2 = pardt1 * ck2 * pom2
        pardt4 = ck4 * 1.25 * pom2 * pom2 * Params.xnodp
        Params.xmdt1 = 0.5 * pardt1 * Params.betao * Params.tthmun
        Params.xgdt1 = -0.5 * pardt1 * Params.unm5th
        Params.xhdt1 = -pardt1 * Params.cosio
        Params.xmdot = Params.xnodp + Params.xmdt1 + pardt2 * .0625 * Params.betao *
                (13.0 - Params.theta2 * 78.0 + theta4 * 137.0)
        Params.omgdot = Params.xgdt1 + pardt2 * .0625 * (7.0 - Params.theta2 *
                114.0 + theta4 * 395.0) + pardt4 * (3.0 - Params.theta2 *
                36.0 + theta4 * 49.0)
        Params.xnodot = Params.xhdt1 + (pardt2 * 0.5 * (4.0 - Params.theta2 * 19.0) + pardt4 *
                2.0 * (3.0 - Params.theta2 * 7.0)) * Params.cosio
    }

    private fun Deep_dpinit(tle: TLE, Params: SatelliteParameters) {
        val zns_per_min = 1.19459E-5
        val zns_per_day = 0.017201977
        val znl_per_day = 0.228027132
        val znl_per_min = 1.5835218E-4
        val thdt = 4.37526908801129966e-3
        val zes = 0.01675
        val zel = 0.05490
        val sinq = Math.sin(tle.xnodeo)
        val cosq = Math.cos(tle.xnodeo)
        val aqnv = 1 / Params.aodp
        val c1ss = 2.9864797E-6
        val days_since_1900 = tle.Epoch - 2415020.0
        var zcosi = 0.91744867
        var zsini = 0.39785416
        var zsing = -0.98088458
        var zcosg = 0.1945905
        var bfact = 0.0
        var cc = c1ss
        var se = 0.0
        var ze = zes
        var zn = zns_per_min
        var sgh = 0.0
        var sh = 0.0
        var si = 0.0
        var zsinh = sinq
        var zcosh = cosq
        var sl = 0.0
        var iteration: Int
        Params.thgr = ThetaG(tle.Epoch)
        Params.xnq = Params.xnodp
        Params.xqncl = tle.xincl
        Params.omegaq = tle.omegao
        run {
            val lunar_asc_node = 4.5236020 - 9.2422029E-4 * days_since_1900
            val sin_asc_node = Math.sin(lunar_asc_node)
            val cos_asc_node = Math.cos(lunar_asc_node)
            val c_minus_gam = znl_per_day * days_since_1900 - 1.1151842
            val gam = 5.8351514 + 0.0019443680 * days_since_1900
            var zx: Double
            val zy: Double
            Params.preep = days_since_1900
            Params.zcosil = 0.91375164 - 0.03568096 * cos_asc_node
            Params.zsinil = Math.sqrt(1.0 - Params.zcosil * Params.zcosil)
            Params.zsinhl = 0.089683511 * sin_asc_node / Params.zsinil
            Params.zcoshl = Math.sqrt(1.0 - Params.zsinhl * Params.zsinhl)
            Params.zmol = Utilities.FMod2p(c_minus_gam)
            zx = 0.39785416 * sin_asc_node / Params.zsinil
            zy = Params.zcoshl * cos_asc_node + 0.91744867 * Params.zsinhl * sin_asc_node
            zx = Math.atan2(zx, zy) + gam - lunar_asc_node
            Params.zcosgl = Math.cos(zx)
            Params.zsingl = Math.sin(zx)
            Params.zmos = Utilities.FMod2p(6.2565837 + zns_per_day * days_since_1900)
        }
        Params.savtsn = 1E20
        iteration = 0
        while (iteration < 2) {
            val c1l = 4.7968065E-7
            val a1 = zcosg * zcosh + zsing * zcosi * zsinh
            val a3 = -zsing * zcosh + zcosg * zcosi * zsinh
            val a7 = -zcosg * zsinh + zsing * zcosi * zcosh
            val a8 = zsing * zsini
            val a9 = zsing * zsinh + zcosg * zcosi * zcosh
            val a10 = zcosg * zsini
            val a2 = Params.cosio * a7 + Params.sinio * a8
            val a4 = Params.cosio * a9 + Params.sinio * a10
            val a5 = -Params.sinio * a7 + Params.cosio * a8
            val a6 = -Params.sinio * a9 + Params.cosio * a10
            val x1 = a1 * Params.cosg + a2 * Params.sing
            val x2 = a3 * Params.cosg + a4 * Params.sing
            val x3 = -a1 * Params.sing + a2 * Params.cosg
            val x4 = -a3 * Params.sing + a4 * Params.cosg
            val x5 = a5 * Params.sing
            val x6 = a6 * Params.sing
            val x7 = a5 * Params.cosg
            val x8 = a6 * Params.cosg
            val z31 = 12 * x1 * x1 - 3 * x3 * x3
            val z32 = 24 * x1 * x2 - 6 * x3 * x4
            val z33 = 12 * x2 * x2 - 3 * x4 * x4
            val z11 = -6 * a1 * a5 + Params.eosq * (-24 * x1 * x7 - 6 * x3 * x5)
            val z12 = -6 * (a1 * a6 + a3 * a5) + Params.eosq * (-24 * (x2 * x7 + x1 * x8) - 6 * (x3 * x6 + x4 * x5))
            val z13 = -6 * a3 * a6 + Params.eosq * (-24 * x2 * x8 - 6 * x4 * x6)
            val z21 = 6 * a2 * a5 + Params.eosq * (24 * x1 * x5 - 6 * x3 * x7)
            val z22 = 6 * (a4 * a5 + a2 * a6) + Params.eosq * (24 * (x2 * x5 + x1 * x6) - 6 * (x4 * x7 + x3 * x8))
            val z23 = 6 * a4 * a6 + Params.eosq * (24 * x2 * x6 - 6 * x4 * x8)
            val s3 = cc / Params.xnq
            val s2 = -0.5 * s3 / Params.betao
            val s4 = s3 * Params.betao
            val s1 = -15 * tle.eo * s4
            val s5 = x1 * x3 + x2 * x4
            val s6 = x2 * x3 + x1 * x4
            val s7 = x2 * x4 - x1 * x3
            var z1 = 3 * (a1 * a1 + a2 * a2) + z31 * Params.eosq
            var z2 = 6 * (a1 * a3 + a2 * a4) + z32 * Params.eosq
            var z3 = 3 * (a3 * a3 + a4 * a4) + z33 * Params.eosq
            z1 = z1 + z1 + Params.betao2 * z31
            z2 = z2 + z2 + Params.betao2 * z32
            z3 = z3 + z3 + Params.betao2 * z33
            se = s1 * zn * s5
            si = s2 * zn * (z11 + z13)
            sl = -zn * s3 * (z1 + z3 - 14 - 6 * Params.eosq)
            sgh = s4 * zn * (z31 + z33 - 6)
            sh = if (Params.xqncl < PI / 60.0) 0.0 else -zn * s2 * (z21 + z23)
            Params.ee2 = 2 * s1 * s6
            Params.e3 = 2 * s1 * s7
            Params.xi2 = 2 * s2 * z12
            Params.xi3 = 2 * s2 * (z13 - z11)
            Params.xl2 = -2 * s3 * z2
            Params.xl3 = -2 * s3 * (z3 - z1)
            Params.xl4 = -2 * s3 * (-21 - 9 * Params.eosq) * ze
            Params.xgh2 = 2 * s4 * z32
            Params.xgh3 = 2 * s4 * (z33 - z31)
            Params.xgh4 = -18 * s4 * ze
            Params.xh2 = -2 * s2 * z22
            Params.xh3 = -2 * s2 * (z23 - z21)
            if (iteration == 0) {
                Params.sse = se
                Params.ssi = si
                Params.ssl = sl
                Params.ssh = if (Params.sinio != 0.0) sh / Params.sinio else 0.0
                Params.ssg = sgh - Params.cosio * Params.ssh
                Params.se2 = Params.ee2
                Params.si2 = Params.xi2
                Params.sl2 = Params.xl2
                Params.sgh2 = Params.xgh2
                Params.sh2 = Params.xh2
                Params.se3 = Params.e3
                Params.si3 = Params.xi3
                Params.sl3 = Params.xl3
                Params.sgh3 = Params.xgh3
                Params.sh3 = Params.xh3
                Params.sl4 = Params.xl4
                Params.sgh4 = Params.xgh4
                zcosg = Params.zcosgl
                zsing = Params.zsingl
                zcosi = Params.zcosil
                zsini = Params.zsinil
                zcosh = Params.zcoshl * cosq + Params.zsinhl * sinq
                zsinh = sinq * Params.zcoshl - cosq * Params.zsinhl
                zn = znl_per_min
                cc = c1l
                ze = zel
            }
            iteration++
        }
        Params.sse += se
        Params.ssi += si
        Params.ssl += sl
        Params.ssg += sgh
        if (Params.sinio != 0.0) {
            Params.ssg -= sh * Params.cosio / Params.sinio
            Params.ssh += sh / Params.sinio
        }
        if (Params.xnq >= 0.00826 && Params.xnq <= 0.00924 && tle.eo >= .5) {
            val root22 = 1.7891679E-6
            val root32 = 3.7393792E-7
            val root44 = 7.3636953E-9
            val root52 = 1.1428639E-7
            val root54 = 2.1765803E-9
            val g201 = -0.306 - (tle.eo - 0.64) * 0.440
            val sini2 = Params.sinio * Params.sinio
            val f220 = 0.75 * (1 + 2 * Params.cosio + Params.theta2)
            val f221 = 1.5 * sini2
            val f321 = 1.875 * Params.sinio * (1 - 2 * Params.cosio - 3 * Params.theta2)
            val f322 = -1.875 * Params.sinio * (1 + 2 * Params.cosio - 3 * Params.theta2)
            val f441 = 35 * sini2 * f220
            val f442 = 39.3750 * sini2 * sini2
            val f522 = 9.84375 * Params.sinio * (sini2 * (1 - 2 * Params.cosio - 5 *
                    Params.theta2) + 0.33333333 * (-2 + 4 * Params.cosio + 6 * Params.theta2))
            val f523 = Params.sinio * (4.92187512 * sini2 * (-2 - 4 *
                    Params.cosio + 10 * Params.theta2) + 6.56250012
                    * (1 + 2 * Params.cosio - 3 * Params.theta2))
            val f542 = 29.53125 * Params.sinio * (2 - 8 *
                    Params.cosio + Params.theta2 *
                    (-12 + 8 * Params.cosio + 10 * Params.theta2))
            val f543 = 29.53125 * Params.sinio * (-2 - 8 * Params.cosio +
                    Params.theta2 * (12 + 8 * Params.cosio - 10 *
                    Params.theta2))
            val g410: Double
            val g422: Double
            val g520: Double
            val g521: Double
            val g532: Double
            val g533: Double
            val g211: Double
            val g310: Double
            val g322: Double
            var temp: Double
            var temp1: Double
            Params.resonance_flag = 1
            Params.synchronous_flag = 0
            if (tle.eo <= 0.65) {
                g211 = 3.616 - 13.247 * tle.eo + 16.290 * Params.eosq
                g310 = eval_cubic_poly(tle.eo, -19.302, 117.390, -228.419, 156.591)
                g322 = eval_cubic_poly(tle.eo, -18.9068, 109.7927, -214.6334, 146.5816)
                g410 = eval_cubic_poly(tle.eo, -41.122, 242.694, -471.094, 313.953)
                g422 = eval_cubic_poly(tle.eo, -146.407, 841.880, -1629.014, 1083.435)
                g520 = eval_cubic_poly(tle.eo, -532.114, 3017.977, -5740.032, 3708.276)
            } else {
                g211 = eval_cubic_poly(tle.eo, -72.099, 331.819, -508.738, 266.724)
                g310 = eval_cubic_poly(tle.eo, -346.844, 1582.851, -2415.925, 1246.113)
                g322 = eval_cubic_poly(tle.eo, -342.585, 1554.908, -2366.899, 1215.972)
                g410 = eval_cubic_poly(tle.eo, -1052.797, 4758.686, -7193.992, 3651.957)
                g422 = eval_cubic_poly(tle.eo, -3581.69, 16178.11, -24462.77, 12422.52)
                g520 = if (tle.eo <= 0.715) eval_cubic_poly(tle.eo, 1464.74, -4664.75, 3763.64, 0.0) else eval_cubic_poly(tle.eo, -5149.66, 29936.92, -54087.36, 31324.56)
            }
            if (tle.eo < 0.7) {
                g533 = eval_cubic_poly(tle.eo, -919.2277, 4988.61, -9064.77, 5542.21)
                g521 = eval_cubic_poly(tle.eo, -822.71072, 4568.6173, -8491.4146, 5337.524)
                g532 = eval_cubic_poly(tle.eo, -853.666, 4690.25, -8624.77, 5341.4)
            } else {
                g533 = eval_cubic_poly(tle.eo, -37995.78, 161616.52, -229838.2, 109377.94)
                g521 = eval_cubic_poly(tle.eo, -51752.104, 218913.95, -309468.16, 146349.42)
                g532 = eval_cubic_poly(tle.eo, -40023.88, 170470.89, -242699.48, 115605.82)
            }
            temp1 = 3 * Params.xnq * Params.xnq * aqnv * aqnv
            temp = temp1 * root22
            Params.d2201 = temp * f220 * g201
            Params.d2211 = temp * f221 * g211
            temp1 *= aqnv
            temp = temp1 * root32
            Params.d3210 = temp * f321 * g310
            Params.d3222 = temp * f322 * g322
            temp1 *= aqnv
            temp = 2 * temp1 * root44
            Params.d4410 = temp * f441 * g410
            Params.d4422 = temp * f442 * g422
            temp1 *= aqnv
            temp = temp1 * root52
            Params.d5220 = temp * f522 * g520
            Params.d5232 = temp * f523 * g532
            temp = 2 * temp1 * root54
            Params.d5421 = temp * f542 * g521
            Params.d5433 = temp * f543 * g533
            Params.xlamo = tle.xmo + tle.xnodeo + tle.xnodeo - Params.thgr - Params.thgr
            bfact = Params.xmdot + Params.xnodot + Params.xnodot - thdt - thdt
            bfact += Params.ssl + Params.ssh + Params.ssh
        } else if (Params.xnq < 1.2 * twopi / MINUTES_PER_DAY && Params.xnq > 0.8 * twopi / MINUTES_PER_DAY) {
            val q22 = 1.7891679E-6
            val q31 = 2.1460748E-6
            val q33 = 2.2123015E-7
            val cosio_plus_1 = 1.0 + Params.cosio
            val g200 = 1 + Params.eosq * (-2.5 + 0.8125 * Params.eosq)
            val g300 = 1 + Params.eosq * (-6 + 6.60937 * Params.eosq)
            val f311 = 0.9375 * Params.sinio * Params.sinio * (1 + 3 * Params.cosio) - 0.75 * cosio_plus_1
            val g310 = 1 + 2 * Params.eosq
            val f220 = 0.75 * cosio_plus_1 * cosio_plus_1
            val f330 = 2.5 * f220 * cosio_plus_1
            Params.synchronous_flag = 1
            Params.resonance_flag = Params.synchronous_flag
            Params.del1 = 3 * Params.xnq * Params.xnq * aqnv * aqnv
            Params.del2 = 2 * Params.del1 * f220 * g200 * q22
            Params.del3 = 3 * Params.del1 * f330 * g300 * q33 * aqnv
            Params.del1 *= f311 * g310 * q31 * aqnv
            Params.xlamo = tle.xmo + tle.xnodeo + tle.omegao - Params.thgr
            bfact = Params.xmdot + Params.omgdot + Params.xnodot - thdt
            bfact = bfact + Params.ssl + Params.ssg + Params.ssh
        } else {
            Params.synchronous_flag = 0
            Params.resonance_flag = Params.synchronous_flag
        }
        if (Params.resonance_flag != 0) {
            Params.xfact = bfact - Params.xnq
            Params.xli = Params.xlamo
            Params.xni = Params.xnq
            Params.atime = 0.0
        }
    }

    private fun eval_cubic_poly(x: Double, constant: Double, linear: Double, quadratic_term: Double, cubic_term: Double): Double {
        return constant + x * (linear + x * (quadratic_term + x * cubic_term))
    }

    private class SatelliteParameters {
        var tthmun = 0.0
        var sini2 = 0.0
        var cosi2 = 0.0
        var unm5th = 0.0
        var unmth2 = 0.0
        var xmdt1 = 0.0
        var xgdt1 = 0.0
        var xhdt1 = 0.0
        var xndt = 0.0
        var edot = 0.0
        var aodp = 0.0
        var cosio = 0.0
        var sinio = 0.0
        var omgdot = 0.0
        var xmdot = 0.0
        var xnodot = 0.0
        var xnodp = 0.0
        var eosq = 0.0
        var betao = 0.0
        var theta2 = 0.0
        var sing = 0.0
        var cosg = 0.0
        var betao2 = 0.0
        var xll = 0.0
        var omgadf = 0.0
        var xnode = 0.0
        var em = 0.0
        var xinc = 0.0
        var xn = 0.0
        var t = 0.0

        /* 'd####' secular coeffs for 12-hour, e>.5 orbits: */
        var d2201 = 0.0
        var d2211 = 0.0
        var d3210 = 0.0
        var d3222 = 0.0
        var d4410 = 0.0
        var d4422 = 0.0
        var d5220 = 0.0
        var d5232 = 0.0
        var d5421 = 0.0
        var d5433 = 0.0

        /* formerly static to Deep( ), but more logically part of this struct: */
        var atime = 0.0
        var del1 = 0.0
        var del2 = 0.0
        var del3 = 0.0
        var e3 = 0.0
        var ee2 = 0.0
        var omegaq = 0.0
        var pe = 0.0
        var pgh = 0.0
        var ph = 0.0
        var pinc = 0.0
        var pl = 0.0
        var preep = 0.0
        var savtsn = 0.0
        var se2 = 0.0
        var se3 = 0.0
        var sgh2 = 0.0
        var sgh3 = 0.0
        var sgh4 = 0.0
        var sh2 = 0.0
        var sh3 = 0.0
        var si2 = 0.0
        var si3 = 0.0
        var sl2 = 0.0
        var sl3 = 0.0
        var sl4 = 0.0
        var sse = 0.0
        var ssg = 0.0
        var ssh = 0.0
        var ssi = 0.0
        var ssl = 0.0
        var thgr = 0.0
        var xfact = 0.0
        var xgh2 = 0.0
        var xgh3 = 0.0
        var xgh4 = 0.0
        var xh2 = 0.0
        var xh3 = 0.0
        var xi2 = 0.0
        var xi3 = 0.0
        var xl2 = 0.0
        var xl3 = 0.0
        var xl4 = 0.0
        var xlamo = 0.0
        var xli = 0.0
        var xni = 0.0
        var xnq = 0.0
        var xqncl = 0.0
        var zcosgl = 0.0
        var zcoshl = 0.0
        var zcosil = 0.0
        var zmol = 0.0
        var zmos = 0.0
        var zsingl = 0.0
        var zsinhl = 0.0
        var zsinil = 0.0
        var resonance_flag = 0
        var synchronous_flag = 0
    }

    private object Utilities {
        private const val dpsec_integration_step = 720.0
        private const val dpsec_integration_order = 2
        private const val zns_per_min = 1.19459E-5
        private const val znl_per_min = 1.5835218E-4
        private const val thdt = 4.37526908801129966e-3
        private const val zes = 0.01675
        private const val zel = 0.05490
        fun Deep_dpsec(tle: TLE, Params: SatelliteParameters) {
            val derivs = DoubleArray(20)
            val temp: Double
            var xni: Double
            var xli: Double
            var final_integration_step = 0
            Params.xll += Params.ssl * Params.t
            Params.omgadf += Params.ssg * Params.t
            Params.xnode += Params.ssh * Params.t
            Params.em = tle.eo + Params.sse * Params.t
            Params.xinc = tle.xincl + Params.ssi * Params.t
            if (Params.resonance_flag == 0) return
            if (Math.abs(Params.t) < Math.abs(Params.t - Params.atime)) {
                Params.atime = 0.0
                xni = Params.xnq
                xli = Params.xlamo
            } else {
                xni = Params.xni
                xli = Params.xli
            }
            while (final_integration_step == 0) {
                var xldot: Double
                var xlpow = 1.0
                var delt_factor: Double
                var delt = Params.t - Params.atime
                var i: Int
                Params.xni = xni
                Params.xli = xli
                compute_dpsec_derivs(Params, derivs)
                if (delt > dpsec_integration_step) delt = dpsec_integration_step else if (delt < -dpsec_integration_step) delt = -dpsec_integration_step else final_integration_step = 1
                xldot = xni + Params.xfact
                xli += delt * xldot
                xni += delt * derivs[0]
                delt_factor = delt
                i = 2
                while (i <= dpsec_integration_order) {
                    xlpow *= xldot
                    derivs[i - 1] *= xlpow
                    delt_factor *= delt / i.toDouble()
                    xli += delt_factor * derivs[i - 2]
                    xni += delt_factor * derivs[i - 1]
                    i++
                }
                if (final_integration_step == 0) {
                    Params.xni = xni
                    Params.xli = xli
                    Params.atime += delt
                }
            }
            Params.xn = xni
            temp = -Params.xnode + Params.thgr + Params.t * thdt
            Params.xll = xli + temp + if (Params.synchronous_flag != 0) -Params.omgadf else temp
        }

        private fun compute_dpsec_derivs(Params: SatelliteParameters, derivs: DoubleArray) {
            val sin_li = Math.sin(Params.xli)
            val cos_li = Math.cos(Params.xli)
            val sin_2li = 2.0 * sin_li * cos_li
            val cos_2li = 2.0 * cos_li * cos_li - 1.0
            var i: Int
            var dpnt = 0
            if (Params.synchronous_flag != 0) {
                val c_fasx2 = 0.99139134268488593
                val s_fasx2 = 0.13093206501640101
                val c_2fasx4 = 0.87051638752972937
                val s_2fasx4 = -0.49213943048915526
                val c_3fasx6 = 0.43258117585763334
                val s_3fasx6 = 0.90159499016666422
                val sin_3li = sin_2li * cos_li + cos_2li * sin_li
                val cos_3li = cos_2li * cos_li - sin_2li * sin_li
                var term1a = Params.del1 * (sin_li * c_fasx2 - cos_li * s_fasx2)
                var term2a = Params.del2 * (sin_2li * c_2fasx4 - cos_2li * s_2fasx4)
                var term3a = Params.del3 * (sin_3li * c_3fasx6 - cos_3li * s_3fasx6)
                var term1b = Params.del1 * (cos_li * c_fasx2 + sin_li * s_fasx2)
                var term2b = 2.0 * Params.del2 * (cos_2li * c_2fasx4 + sin_2li * s_2fasx4)
                var term3b = 3.0 * Params.del3 * (cos_3li * c_3fasx6 + sin_3li * s_3fasx6)
                i = 0
                while (i < dpsec_integration_order) {
                    derivs[dpnt++] = term1a + term2a + term3a
                    derivs[dpnt++] = term1b + term2b + term3b
                    if (i + 2 < dpsec_integration_order) {
                        term1a = -term1a
                        term2a *= -4.0
                        term3a *= -9.0
                        term1b = -term1b
                        term2b *= -4.0
                        term3b *= -9.0
                    }
                    i += 2
                }
            } else {
                val c_g22 = 0.87051638752972937
                val s_g22 = -0.49213943048915526
                val c_g32 = 0.57972190187001149
                val s_g32 = 0.81481440616389245
                val c_g44 = -0.22866241528815548
                val s_g44 = 0.97350577801807991
                val c_g52 = 0.49684831179884198
                val s_g52 = 0.86783740128127729
                val c_g54 = -0.29695209575316894
                val s_g54 = -0.95489237761529999
                val xomi = Params.omegaq + Params.omgdot * Params.atime
                val sin_omi = Math.sin(xomi)
                val cos_omi = Math.cos(xomi)
                val sin_li_m_omi = sin_li * cos_omi - sin_omi * cos_li
                val sin_li_p_omi = sin_li * cos_omi + sin_omi * cos_li
                val cos_li_m_omi = cos_li * cos_omi + sin_omi * sin_li
                val cos_li_p_omi = cos_li * cos_omi - sin_omi * sin_li
                val sin_2omi = 2.0 * sin_omi * cos_omi
                val cos_2omi = 2.0 * cos_omi * cos_omi - 1.0
                val sin_2li_m_omi = sin_2li * cos_omi - sin_omi * cos_2li
                val sin_2li_p_omi = sin_2li * cos_omi + sin_omi * cos_2li
                val cos_2li_m_omi = cos_2li * cos_omi + sin_omi * sin_2li
                val cos_2li_p_omi = cos_2li * cos_omi - sin_omi * sin_2li
                val sin_2li_p_2omi = sin_2li * cos_2omi + sin_2omi * cos_2li
                val cos_2li_p_2omi = cos_2li * cos_2omi - sin_2omi * sin_2li
                val sin_2omi_p_li = sin_li * cos_2omi + sin_2omi * cos_li
                val cos_2omi_p_li = cos_li * cos_2omi - sin_2omi * sin_li
                var term1a = Params.d2201 * (sin_2omi_p_li * c_g22 - cos_2omi_p_li * s_g22) + Params.d2211 * (sin_li * c_g22 - cos_li * s_g22) + Params.d3210 * (sin_li_p_omi * c_g32 - cos_li_p_omi * s_g32) + Params.d3222 * (sin_li_m_omi * c_g32 - cos_li_m_omi * s_g32) + Params.d5220 * (sin_li_p_omi * c_g52 - cos_li_p_omi * s_g52) + Params.d5232 * (sin_li_m_omi * c_g52 - cos_li_m_omi * s_g52)
                var term2a = Params.d4410 * (sin_2li_p_2omi * c_g44 - cos_2li_p_2omi * s_g44) + Params.d4422 * (sin_2li * c_g44 - cos_2li * s_g44) + Params.d5421 * (sin_2li_p_omi * c_g54 - cos_2li_p_omi * s_g54) + Params.d5433 * (sin_2li_m_omi * c_g54 - cos_2li_m_omi * s_g54)
                var term1b = Params.d2201 * (cos_2omi_p_li * c_g22 + sin_2omi_p_li * s_g22) + Params.d2211 * (cos_li * c_g22 + sin_li * s_g22) + Params.d3210 * (cos_li_p_omi * c_g32 + sin_li_p_omi * s_g32) + Params.d3222 * (cos_li_m_omi * c_g32 + sin_li_m_omi * s_g32) + Params.d5220 * (cos_li_p_omi * c_g52 + sin_li_p_omi * s_g52) + Params.d5232 * (cos_li_m_omi * c_g52 + sin_li_m_omi * s_g52)
                var term2b = 2.0 *
                        (Params.d4410 * (cos_2li_p_2omi * c_g44 + sin_2li_p_2omi * s_g44) + Params.d4422 * (cos_2li * c_g44 + sin_2li * s_g44) + Params.d5421 * (cos_2li_p_omi * c_g54 + sin_2li_p_omi * s_g54) + Params.d5433 * (cos_2li_m_omi * c_g54 + sin_2li_m_omi * s_g54))
                i = 0
                while (i < dpsec_integration_order) {
                    derivs[dpnt++] = term1a + term2a
                    derivs[dpnt++] = term1b + term2b
                    if (i + 2 < dpsec_integration_order) {
                        term1a = -term1a
                        term2a *= -4.0
                        term1b = -term1b
                        term2b *= -4.0
                    }
                    i += 2
                }
            }
        }

        fun Deep_dpper(tle: TLE, Params: SatelliteParameters) {
            var sinis: Double
            var cosis: Double
            if (Math.abs(Params.savtsn - Params.t) >= 30.0) {
                var zf: Double
                var zm: Double
                var sinzf: Double
                val ses: Double
                val sis: Double
                val sil: Double
                val sel: Double
                val sll: Double
                val sls: Double
                var f2: Double
                var f3: Double
                val sghl: Double
                val sghs: Double
                val shs: Double
                val sh1: Double
                Params.savtsn = Params.t
                zm = Params.zmos + zns_per_min * Params.t
                zf = zm + 2 * zes * Math.sin(zm)
                sinzf = Math.sin(zf)
                f2 = 0.5 * sinzf * sinzf - 0.25
                f3 = -0.5 * sinzf * Math.cos(zf)
                ses = Params.se2 * f2 + Params.se3 * f3
                sis = Params.si2 * f2 + Params.si3 * f3
                sls = Params.sl2 * f2 + Params.sl3 * f3 + Params.sl4 * sinzf
                sghs = Params.sgh2 * f2 + Params.sgh3 * f3 + Params.sgh4 * sinzf
                shs = Params.sh2 * f2 + Params.sh3 * f3
                zm = Params.zmol + znl_per_min * Params.t
                zf = zm + 2 * zel * Math.sin(zm)
                sinzf = Math.sin(zf)
                f2 = 0.5 * sinzf * sinzf - 0.25
                f3 = -0.5 * sinzf * Math.cos(zf)
                sel = Params.ee2 * f2 + Params.e3 * f3
                sil = Params.xi2 * f2 + Params.xi3 * f3
                sll = Params.xl2 * f2 + Params.xl3 * f3 + Params.xl4 * sinzf
                sghl = Params.xgh2 * f2 + Params.xgh3 * f3 + Params.xgh4 * sinzf
                sh1 = Params.xh2 * f2 + Params.xh3 * f3
                Params.pe = ses + sel
                Params.pinc = sis + sil
                Params.pl = sls + sll
                Params.pgh = sghs + sghl
                Params.ph = shs + sh1
            }
            Params.xinc += Params.pinc
            sinis = Math.sin(Params.xinc)
            cosis = Math.cos(Params.xinc)
            Params.em += Params.pe
            Params.xll += Params.pl
            Params.omgadf += Params.pgh
            if (tle.xincl >= 0.2) {
                sinis = Math.sin(Params.xinc)
                cosis = Math.cos(Params.xinc)
                val temp_val = Params.ph / sinis
                Params.omgadf -= cosis * temp_val
                Params.xnode += temp_val
            } else {
                val sinok = Math.sin(Params.xnode)
                val cosok = Math.cos(Params.xnode)
                val alfdp = Params.ph * cosok + (Params.pinc * cosis + sinis) * sinok
                val betdp = -Params.ph * sinok + (Params.pinc * cosis + sinis) * cosok
                val dls: Double
                var delta_xnode: Double
                delta_xnode = Math.atan2(alfdp, betdp) - Params.xnode
                if (delta_xnode < -PI) delta_xnode += twopi else if (delta_xnode > PI) delta_xnode -= twopi
                dls = -Params.xnode * sinis * Params.pinc
                Params.omgadf += dls - cosis * delta_xnode
                Params.xnode += delta_xnode
            }
        }

        fun CommonInitialization(tle: TLE, Params: SatelliteParameters) {
            val a1 = Math.pow(xke / tle.xno, two_thirds)
            val del1: Double
            val ao: Double
            val delo: Double
            val x3thm1: Double
            val tval: Double
            Params.cosio = Math.cos(tle.xincl)
            Params.theta2 = Params.cosio * Params.cosio
            x3thm1 = 3.0 * Params.theta2 - 1.0
            Params.eosq = tle.eo * tle.eo
            Params.betao2 = 1 - Params.eosq
            Params.betao = Math.sqrt(Params.betao2)
            tval = 1.5 * ck2 * x3thm1 / (Params.betao * Params.betao2)
            del1 = tval / (a1 * a1)
            ao = a1 * (1 - del1 * (0.5 * two_thirds + del1 * (1.0 + 134.0 / 81.0 * del1)))
            delo = tval / (ao * ao)
            Params.xnodp = tle.xno / (1 + delo)
            Params.aodp = ao / (1 - delo)
        }

        fun FMod2p(x: Double): Double {
            var rval = fmod(x, PT2)
            if (rval < 0.0) rval += PT2
            return rval
        }

        internal fun fmod(x: Double, y: Double): Double {
            return Math.IEEEremainder(x, y)
        }
    }
}