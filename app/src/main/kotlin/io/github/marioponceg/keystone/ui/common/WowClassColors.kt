package io.github.marioponceg.keystone.ui.common

import androidx.compose.ui.graphics.Color

/** Blizzard's official class colors, keyed by the class name Raider.IO returns. */
object WowClassColors {
    private val colors = mapOf(
        "Death Knight" to Color(0xFFC41E3A),
        "Demon Hunter" to Color(0xFFA330C9),
        "Druid" to Color(0xFFFF7C0A),
        "Evoker" to Color(0xFF33937F),
        "Hunter" to Color(0xFFAAD372),
        "Mage" to Color(0xFF3FC7EB),
        "Monk" to Color(0xFF00FF98),
        "Paladin" to Color(0xFFF48CBA),
        "Priest" to Color(0xFFFFFFFF),
        "Rogue" to Color(0xFFFFF468),
        "Shaman" to Color(0xFF0070DD),
        "Warlock" to Color(0xFF8788EE),
        "Warrior" to Color(0xFFC69B6D),
    )

    fun forClass(name: String, fallback: Color): Color = colors[name] ?: fallback
}
