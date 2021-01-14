package com.stho.nyota.sky.universe

// TODO: move to another class and make symbol public
enum class Symbol {
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
    Omichron,
    Pi,
    RHO,
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
    FortyOne;

    companion object {

        fun greekSymbolToString(symbol: Symbol): String =
            when (symbol) {
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
                Symbol.Omichron -> "ο"
                Symbol.Pi -> "π"
                Symbol.RHO -> "ρ"
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
            }

        fun greekSymbolImageId(symbol: Symbol): Int =
            when (symbol) {
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
                Symbol.Omichron -> com.stho.nyota.R.drawable.greek_omicron
                Symbol.Pi -> com.stho.nyota.R.drawable.greek_pi
                Symbol.RHO -> com.stho.nyota.R.drawable.greek_rho
                Symbol.Sigma -> com.stho.nyota.R.drawable.greek_sigma
                Symbol.Tau -> com.stho.nyota.R.drawable.greek_tau
                Symbol.Upsilon -> com.stho.nyota.R.drawable.greek_upsilon
                Symbol.Phi -> com.stho.nyota.R.drawable.greek_phi
                Symbol.Chi -> com.stho.nyota.R.drawable.greek_chi
                Symbol.Psi -> com.stho.nyota.R.drawable.greek_psi
                Symbol.Omega -> com.stho.nyota.R.drawable.greek_omega
                Symbol.One -> com.stho.nyota.R.drawable.star
                Symbol.Two -> com.stho.nyota.R.drawable.star
                Symbol.Three -> com.stho.nyota.R.drawable.star
                Symbol.Four -> com.stho.nyota.R.drawable.star
                Symbol.Five -> com.stho.nyota.R.drawable.star
                Symbol.Six -> com.stho.nyota.R.drawable.star
                Symbol.Seven -> com.stho.nyota.R.drawable.star
                Symbol.Eight -> com.stho.nyota.R.drawable.star
                Symbol.Nine -> com.stho.nyota.R.drawable.star
                Symbol.Eleven -> com.stho.nyota.R.drawable.star
                Symbol.FortyOne -> com.stho.nyota.R.drawable.star
            }
    }
}