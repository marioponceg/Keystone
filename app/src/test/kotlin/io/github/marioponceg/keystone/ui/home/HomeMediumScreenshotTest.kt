package io.github.marioponceg.keystone.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The layout axis of the screenshot matrix: the representative Content state only, at a medium
 * window width. State variants stay in [HomeScreenshotTest] at compact width — a NotFound or
 * Unavailable state does not break differently at 720dp than at 411dp; what breaks with width is
 * layout, not state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// 720dp sits inside the medium bucket (600..839) — wide enough for two columns, narrow enough
// that it is not the expanded two-pane case covered by KeystoneShellScreenshotTest.
@Config(sdk = [35], qualifiers = "w720dp-h1200dp")
class HomeMediumScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Index 1 is the Content state: form filled, recents populated.
    private val contentState = HomeStateProvider().values.toList()[1]

    // Index 0 is the empty state — a fresh install, nothing searched yet.
    private val emptyState = HomeStateProvider().values.toList()[0]

    private fun capture(name: String, darkTheme: Boolean, state: HomeUiState = contentState) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                HomeContent(state = state, onEvent = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun homeMediumLight() = capture("home_medium_content_light", false)

    @Test
    fun homeMediumDark() = capture("home_medium_content_dark", true)

    /**
     * First run at medium width, and a layout-axis case rather than a state-axis one: the
     * two-column layout branches on whether recents are empty, so this captures a different
     * arrangement, not merely a different state rendered the same way. Without it nothing guards
     * the right column against going blank again.
     */
    @Test
    fun homeMediumEmptyRecentsLight() =
        capture("home_medium_empty_recents_light", false, emptyState)

    @Test
    fun homeMediumEmptyRecentsDark() =
        capture("home_medium_empty_recents_dark", true, emptyState)
}
