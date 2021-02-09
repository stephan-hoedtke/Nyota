package com.stho.nyota.sky.universe

// TODO: move to another class and make symbol public
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
    P,
    Q;

    fun isSymbol(): Boolean =
        this != NoSymbol

    val greekSymbol: String
        get() = Symbol.greekSymbolToString(this) // Mind, this is an enum class: do not define as greekSymbol using "="

    val imageId: Int
        get() = Symbol.greekSymbolImageId(this) // Mind, this is an enum class: do not define as imageId using "="

    companion object {

        private fun greekSymbolToString(symbol: Symbol): String =
            when (symbol) {
                Symbol.NoSymbol -> ""
                Symbol.Alpha -> "α"
                Symbol.Beta -> "β"
                Symbol.Gamma -> "γ"
                Symbol.Delta -> "δ"
                Symbol.Epsilon -> "ε"
                Symbol.Zeta -> "ζ"
                Symbol.Eta -> "η"
                Symbol.Theta -> "θ"
                Symbol.Iota -> "ι"
                Symbol.Kappa -> "κ"
                Symbol.Lambda -> "λ"
                Symbol.Mu -> "μ"
                Symbol.Nu -> "ν"
                Symbol.Xi -> "ξ"
                Symbol.Omicron -> "ο"
                Symbol.Pi -> "π"
                Symbol.Rho -> "ρ"
                Symbol.Sigma -> "σ"
                Symbol.Tau -> "τ"
                Symbol.Upsilon -> "υ"
                Symbol.Phi -> "φ"
                Symbol.Chi -> "χ"
                Symbol.Psi -> "ψ"
                Symbol.Omega -> "ω"
                Symbol.One -> "1"
                Symbol.Two -> "2"
                Symbol.Three -> "3"
                Symbol.Four -> "4"
                Symbol.Five -> "5"
                Symbol.Six -> "6"
                Symbol.Seven -> "7"
                Symbol.Eight -> "8"
                Symbol.Nine -> "9"
                Symbol.Eleven -> "11"
                Symbol.FortyOne -> "41"
                Symbol.P -> "P"
                Symbol.Q -> "Q"
            }

        private fun greekSymbolImageId(symbol: Symbol): Int =
            when (symbol) {
                Symbol.NoSymbol -> com.stho.nyota.R.drawable.empty
                Symbol.Alpha -> com.stho.nyota.R.drawable.greek_alpha
                Symbol.Beta -> com.stho.nyota.R.drawable.greek_beta
                Symbol.Gamma -> com.stho.nyota.R.drawable.greek_gamma
                Symbol.Delta -> com.stho.nyota.R.drawable.greek_delta
                Symbol.Epsilon -> com.stho.nyota.R.drawable.greek_epsilon
                Symbol.Zeta -> com.stho.nyota.R.drawable.greek_zeta
                Symbol.Eta -> com.stho.nyota.R.drawable.greek_eta
                Symbol.Theta -> com.stho.nyota.R.drawable.greek_theta
                Symbol.Iota -> com.stho.nyota.R.drawable.greek_iota
                Symbol.Kappa -> com.stho.nyota.R.drawable.greek_kappa
                Symbol.Lambda -> com.stho.nyota.R.drawable.greek_lambda
                Symbol.Mu -> com.stho.nyota.R.drawable.greek_mu
                Symbol.Nu -> com.stho.nyota.R.drawable.greek_nu
                Symbol.Xi -> com.stho.nyota.R.drawable.greek_xi
                Symbol.Omicron -> com.stho.nyota.R.drawable.greek_omicron
                Symbol.Pi -> com.stho.nyota.R.drawable.greek_pi
                Symbol.Rho -> com.stho.nyota.R.drawable.greek_rho
                Symbol.Sigma -> com.stho.nyota.R.drawable.greek_sigma
                Symbol.Tau -> com.stho.nyota.R.drawable.greek_tau
                Symbol.Upsilon -> com.stho.nyota.R.drawable.greek_upsilon
                Symbol.Phi -> com.stho.nyota.R.drawable.greek_phi
                Symbol.Chi -> com.stho.nyota.R.drawable.greek_chi
                Symbol.Psi -> com.stho.nyota.R.drawable.greek_psi
                Symbol.Omega -> com.stho.nyota.R.drawable.greek_omega
                else -> com.stho.nyota.R.drawable.star
            }

        fun fromString(symbol: String): Symbol {
            if (symbol.isNotBlank()) {
                try {
                    return Symbol.valueOf(symbol)
                } catch (ex: Exception) {
                    // Ignore
                }
            }
            return Symbol.NoSymbol
        }
    }
}