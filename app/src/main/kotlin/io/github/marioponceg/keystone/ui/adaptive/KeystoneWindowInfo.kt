package io.github.marioponceg.keystone.ui.adaptive

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass

/**
 * The window-level facts a Keystone screen is allowed to react to.
 *
 * Deliberately *not* a general "how big is the screen" object. Layout that depends on the space a
 * composable was actually given must use [androidx.compose.foundation.layout.BoxWithConstraints]
 * instead: at expanded width the window is ~1280dp while the home pane is ~400dp, so reacting to
 * window width inside a pane draws the wrong layout. This type carries only facts that are true of
 * the window regardless of which pane you are in — whether a dialog is appropriate, and whether the
 * device is folded flat on a table.
 *
 * "Is there a second pane?" deliberately does *not* live here. That fact belongs to the
 * `PaneScaffoldDirective` that actually decides the pane count, and is read off its
 * `maxHorizontalPartitions` — see `KeystoneNavDisplay`. A width breakpoint on this type would be a
 * second, independently derived answer to the same question, agreeing with the directive only by
 * coincidence.
 *
 * Window size never reaches a `UiState`: this is passed to composables, so ViewModels stay
 * width-agnostic.
 */
@Immutable
data class KeystoneWindowInfo(
    val isWidthAtLeastMedium: Boolean,
    val isTabletop: Boolean,
) {
    companion object {
        /** Phone portrait. The default for previews and for the compact-only screenshot goldens. */
        val Compact = KeystoneWindowInfo(
            isWidthAtLeastMedium = false,
            isTabletop = false,
        )
    }
}

/**
 * Pure mapping from platform types to [KeystoneWindowInfo], separated from the composable so it is
 * testable without a window — [WindowSizeClass.compute] and [Posture] can both be built by hand.
 */
fun keystoneWindowInfoOf(
    windowSizeClass: WindowSizeClass,
    posture: Posture,
): KeystoneWindowInfo = KeystoneWindowInfo(
    isWidthAtLeastMedium = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND),
    isTabletop = posture.isTabletop,
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberKeystoneWindowInfo(): KeystoneWindowInfo {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    return remember(adaptiveInfo) {
        keystoneWindowInfoOf(
            windowSizeClass = adaptiveInfo.windowSizeClass,
            posture = adaptiveInfo.windowPosture,
        )
    }
}
