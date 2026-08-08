package io.github.marioponceg.keystone.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp

/**
 * The safe area as `contentPadding` for a scrolling container, widened by [horizontal] of design
 * spacing on each side.
 *
 * Scrolling containers take the safe area here rather than through a padding modifier: padding the
 * container clips the list to the safe area, so its rows can no longer travel behind the status and
 * navigation bars — which is the point of `enableEdgeToEdge`. The design's own horizontal spacing
 * is folded in rather than applied separately, because two sources of horizontal padding on the
 * same list would add up.
 *
 * `safeDrawing` rather than `systemBars`: it also covers display cutouts and the IME, so a text
 * field inside the list stays reachable when the keyboard opens.
 */
@Composable
fun safeDrawingContentPadding(horizontal: Dp): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val safeArea = WindowInsets.safeDrawing.asPaddingValues()
    return PaddingValues(
        start = safeArea.calculateStartPadding(layoutDirection) + horizontal,
        top = safeArea.calculateTopPadding(),
        end = safeArea.calculateEndPadding(layoutDirection) + horizontal,
        bottom = safeArea.calculateBottomPadding(),
    )
}
