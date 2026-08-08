package io.github.marioponceg.keystone.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import io.github.marioponceg.foundry.tokens.FoundryTheme

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

// The scrim runs slightly past the status bar so the gradient finishes fading below it rather than
// stopping at a visible hard line exactly on the boundary.
private const val SCRIM_OVERSHOOT = 1.2f

/**
 * A fade over the status bar, drawn on top of everything else.
 *
 * The counterpart to [safeDrawingContentPadding]: that keeps content *out* of the status bar when
 * the list is at rest, but a scrolling list is supposed to travel *behind* it, and there the clock
 * and the app's own text land on top of each other and neither can be read. This puts the app's
 * background back underneath the system icons, fading out so it never reads as an opaque bar.
 *
 * Draw it as the last child of a full-screen container so it sits above the content it protects.
 */
@Composable
fun StatusBarProtection(
    modifier: Modifier = Modifier,
    color: Color = FoundryTheme.colors.background,
) {
    val height = with(LocalDensity.current) {
        (WindowInsets.statusBars.getTop(this) * SCRIM_OVERSHOOT).toDp()
    }
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.verticalGradient(
                    listOf(color, color.copy(alpha = 0.8f), Color.Transparent),
                ),
            ),
    )
}
