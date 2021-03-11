package com.stho.nyota.sky.universe

import com.stho.nyota.R

enum class StarColor(val imageId: Int) {
    Blue(R.drawable.star_blue),                             // #92b5ff
    DeepBlueWhite(R.drawable.star_deep_blue_white),        // #a2c0ff
    BlueWhite(R.drawable.star_blue_white),                  // #d5e0ff
    White(R.drawable.star_white),                           // #f9f5ff
    YellowishWhite(R.drawable.star_yellowish_white),        // #ffede3
    PaleYellowOrange(R.drawable.star_pale_yellow_orange),   // #ffdab5
    LightOrangeRed(R.drawable.star_light_orange_red);       // #ffb56c

    override fun toString(): String {
        return when (this) {
            Blue -> "O (blue)"
            DeepBlueWhite -> "B (deep blue white)"
            BlueWhite -> "A (blue white)"
            White -> "F (white)"
            YellowishWhite -> "G (yellowish white)"
            PaleYellowOrange -> "K (pale yellow orange)"
            LightOrangeRed -> "M (light orange red)"
        }
    }

    companion object {
        fun parseString(color: String): StarColor =
            when (color) {
                "O" -> Blue
                "B" -> DeepBlueWhite
                "A" -> BlueWhite
                "F" -> White
                "G" -> YellowishWhite
                "K" -> PaleYellowOrange
                "M" -> LightOrangeRed
                else -> White
            }
    }
}

