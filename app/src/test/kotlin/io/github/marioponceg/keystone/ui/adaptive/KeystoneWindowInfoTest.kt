package io.github.marioponceg.keystone.ui.adaptive

import androidx.compose.material3.adaptive.Posture
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KeystoneWindowInfoTest {

    private fun infoFor(widthDp: Float, heightDp: Float = 900f, tabletop: Boolean = false) =
        keystoneWindowInfoOf(
            windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                widthDp = widthDp,
                heightDp = heightDp,
            ),
            posture = Posture(isTabletop = tabletop),
        )

    @Test
    fun phoneWidthIsNotMedium() {
        assertFalse(infoFor(widthDp = 411f).isWidthAtLeastMedium)
    }

    @Test
    fun tabletPortraitIsMedium() {
        assertTrue(infoFor(widthDp = 720f).isWidthAtLeastMedium)
    }

    @Test
    fun desktopWidthIsStillAtLeastMedium() {
        // The property is "at least medium", not "exactly medium": a dialog stays the right
        // component past the expanded breakpoint, so widths above it must not fall back to false.
        assertTrue(infoFor(widthDp = 1280f).isWidthAtLeastMedium)
    }

    @Test
    fun theMediumBreakpointIsInclusive() {
        assertTrue(infoFor(widthDp = 600f).isWidthAtLeastMedium)
        assertFalse(infoFor(widthDp = 599f).isWidthAtLeastMedium)
    }

    @Test
    fun tabletopPostureIsCarriedThrough() {
        assertTrue(infoFor(widthDp = 411f, tabletop = true).isTabletop)
        assertFalse(infoFor(widthDp = 411f, tabletop = false).isTabletop)
    }

    @Test
    fun compactDefaultIsTheSmallestSingleColumnCase() {
        assertEquals(
            KeystoneWindowInfo(
                isWidthAtLeastMedium = false,
                isTabletop = false,
            ),
            KeystoneWindowInfo.Compact,
        )
    }
}
