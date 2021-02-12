package com.stho.nyota.sky.universe

enum class Symbol {
    NoSymbol,
    Alpha,
    Beta,
    Gamma,
    Delta,
    Epsilon,
    Zeta,
    Eta,
    Theta,
    Iota,
    Kappa,
    Lambda,
    Mu,
    Nu,
    Xi,
    Omicron,
    Pi,
    Rho,
    Sigma,
    Tau,
    Upsilon,
    Phi,
    Chi,
    Psi,
    Omega,
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Eleven,
    FortyOne,
    D,
    H,
    N,
    P,
    Q;

    fun isSymbol(): Boolean =
        this != NoSymbol

    val greekSymbol: String
        get() = greekSymbolToString(this) // Mind, this is an enum class: do not define as greekSymbol using "="

    val imageId: Int
        get() = greekSymbolImageId(this) // Mind, this is an enum class: do not define as imageId using "="

    companion object {

        private fun greekSymbolToString(symbol: Symbol): String =
            when (symbol) {
                NoSymbol -> ""
                Alpha -> "α"
                Beta -> "β"
                Gamma -> "γ"
                Delta -> "δ"
                Epsilon -> "ε"
                Zeta -> "ζ"
                Eta -> "η"
                Theta -> "θ"
                Iota -> "ι"
                Kappa -> "κ"
                Lambda -> "λ"
                Mu -> "μ"
                Nu -> "ν"
                Xi -> "ξ"
                Omicron -> "ο"
                Pi -> "π"
                Rho -> "ρ"
                Sigma -> "σ"
                Tau -> "τ"
                Upsilon -> "υ"
                Phi -> "φ"
                Chi -> "χ"
                Psi -> "ψ"
                Omega -> "ω"
                One -> "1"
                Two -> "2"
                Three -> "3"
                Four -> "4"
                Five -> "5"
                Six -> "6"
                Seven -> "7"
                Eight -> "8"
                Nine -> "9"
                Eleven -> "11"
                FortyOne -> "41"
                D -> "D"
                H -> "H"
                N -> "N"
                P -> "P"
                Q -> "Q"
            }

        private fun greekSymbolImageId(symbol: Symbol): Int =
            when (symbol) {
                NoSymbol -> com.stho.nyota.R.drawable.empty
                Alpha -> com.stho.nyota.R.drawable.greek_alpha
                Beta -> com.stho.nyota.R.drawable.greek_beta
                Gamma -> com.stho.nyota.R.drawable.greek_gamma
                Delta -> com.stho.nyota.R.drawable.greek_delta
                Epsilon -> com.stho.nyota.R.drawable.greek_epsilon
                Zeta -> com.stho.nyota.R.drawable.greek_zeta
                Eta -> com.stho.nyota.R.drawable.greek_eta
                Theta -> com.stho.nyota.R.drawable.greek_theta
                Iota -> com.stho.nyota.R.drawable.greek_iota
                Kappa -> com.stho.nyota.R.drawable.greek_kappa
                Lambda -> com.stho.nyota.R.drawable.greek_lambda
                Mu -> com.stho.nyota.R.drawable.greek_mu
                Nu -> com.stho.nyota.R.drawable.greek_nu
                Xi -> com.stho.nyota.R.drawable.greek_xi
                Omicron -> com.stho.nyota.R.drawable.greek_omicron
                Pi -> com.stho.nyota.R.drawable.greek_pi
                Rho -> com.stho.nyota.R.drawable.greek_rho
                Sigma -> com.stho.nyota.R.drawable.greek_sigma
                Tau -> com.stho.nyota.R.drawable.greek_tau
                Upsilon -> com.stho.nyota.R.drawable.greek_upsilon
                Phi -> com.stho.nyota.R.drawable.greek_phi
                Chi -> com.stho.nyota.R.drawable.greek_chi
                Psi -> com.stho.nyota.R.drawable.greek_psi
                Omega -> com.stho.nyota.R.drawable.greek_omega
                else -> com.stho.nyota.R.drawable.star
            }

        fun fromString(symbol: String): Symbol {
            if (symbol.isNotBlank()) {
                try {
                    return valueOf(symbol)
                } catch (ex: Exception) {
                    // Ignore
                }
            }
            return NoSymbol
        }
    }
}