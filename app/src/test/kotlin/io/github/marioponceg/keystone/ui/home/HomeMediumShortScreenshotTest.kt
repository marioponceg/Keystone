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
 * Wide but short — split screen, a half-open foldable, a resized desktop window. The two-column
 * layout does not scroll as a whole, so below [HOME_TWO_COLUMN_MIN_HEIGHT_DP] the scrollable
 * single-column layout wins even though the window is wide enough for two columns.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w720dp-h420dp")
class HomeMediumShortScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val contentState = HomeStateProvider().values.toList()[1]

    private fun capture(name: String, darkTheme: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                HomeContent(state = contentState, onEvent = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun homeMediumShortLight() = capture("home_medium_short_light", false)

    @Test
    fun homeMediumShortDark() = capture("home_medium_short_dark", true)
}
