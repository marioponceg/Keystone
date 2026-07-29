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
    fun phoneWidthIsNeitherMediumNorExpanded() {
        val info = infoFor(widthDp = 411f)
        assertFalse(info.isWidthAtLeastMedium)
        assertFalse(info.isWidthAtLeastExpanded)
    }

    @Test
    fun tabletPortraitIsMediumButNotExpanded() {
        val info = infoFor(widthDp = 720f)
        assertTrue(info.isWidthAtLeastMedium)
        assertFalse(info.isWidthAtLeastExpanded)
    }

    @Test
    fun desktopWidthIsBothMediumAndExpanded() {
        val info = infoFor(widthDp = 1280f)
        assertTrue(info.isWidthAtLeastMedium)
        assertTrue(info.isWidthAtLeastExpanded)
    }

    @Test
    fun exactBreakpointsAreInclusive() {
        assertTrue(infoFor(widthDp = 600f).isWidthAtLeastMedium)
        assertTrue(infoFor(widthDp = 840f).isWidthAtLeastExpanded)
        assertFalse(infoFor(widthDp = 839f).isWidthAtLeastExpanded)
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
                isWidthAtLeastExpanded = false,
                isTabletop = false,
            ),
            KeystoneWindowInfo.Compact,
        )
    }
}
