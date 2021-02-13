package com.stho.nyota.sky.universe


enum class Language {
    Latin,
    German,
    English,
    French,
    Greek;

    companion object {

        fun languageImageId(language: Language): Int =
            when (language) {
                Latin -> com.stho.nyota.R.drawable.language_latin
                German -> com.stho.nyota.R.drawable.language_german
                English -> com.stho.nyota.R.drawable.language_english
                French -> com.stho.nyota.R.drawable.language_french
                Greek -> com.stho.nyota.R.drawable.language_greek
            }
    }
}