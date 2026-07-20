package io.github.marioponceg.keystone.ui.common

import androidx.compose.ui.graphics.Color

private const val HEX_RADIX = 16

/** Parses Raider.IO's "#rrggbb" score colors; falls back on any malformed value. */
fun parseScoreColor(hex: String, fallback: Color): Color =
    runCatching { Color(("ff" + hex.removePrefix("#")).toLong(HEX_RADIX)) }.getOrDefault(fallback)
