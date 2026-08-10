package io.github.marioponceg.keystone.ui.week

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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h1200dp")
class WeekScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val states = WeekStateProvider().values.toList()

    private fun capture(name: String, darkTheme: Boolean, state: WeekUiState) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                WeekContent(state = state, onEvent = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun weekLoadingLight() = capture("week_loading_light", false, states[0])

    @Test
    fun weekLoadingDark() = capture("week_loading_dark", true, states[0])

    @Test
    fun weekContentLight() = capture("week_content_light", false, states[1])

    @Test
    fun weekContentDark() = capture("week_content_dark", true, states[1])

    @Test
    fun weekUnavailableLight() = capture("week_unavailable_light", false, states[2])

    @Test
    fun weekUnavailableDark() = capture("week_unavailable_dark", true, states[2])
}
