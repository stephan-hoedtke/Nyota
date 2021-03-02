package com.stho.nyota

import com.stho.nyota.sky.universe.*

interface INavigable {
    fun onSkyView(element: IElement)
    fun onFinderView(element: IElement)
    fun onMoon()
    fun onSun()
    fun onPlanet(planet: AbstractPlanet)
    fun onStar(star: Star)
    fun onSatellite(satellite: Satellite)
    fun onGalaxy(galaxy: Galaxy)
    fun onConstellation(constellation: Constellation)
    fun showNextStepDialogForElement(element: IElement)
}