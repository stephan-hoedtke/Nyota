package com.stho.nyota.sky.universe


enum class Language {
    Latin,
    German,
    English;

    companion object {

        fun languageImageId(language: Language): Int =
            when (language) {
                Language.Latin -> com.stho.nyota.R.drawable.language_latin
                Language.German -> com.stho.nyota.R.drawable.language_german
                Language.English -> com.stho.nyota.R.drawable.language_english
            }
    }
}