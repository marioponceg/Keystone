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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// sdk pinned to 35: the highest android-all image Robolectric ships (compileSdk is 37).
@Config(sdk = [35])
class HomeScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val states = HomeStateProvider().values.toList()

    private fun capture(name: String, darkTheme: Boolean, state: HomeUiState) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                HomeContent(state = state, onEvent = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun homeEmptyLight() = capture("home_empty_light", false, states[0])

    @Test
    fun homeEmptyDark() = capture("home_empty_dark", true, states[0])

    @Test
    fun homeContentLight() = capture("home_content_light", false, states[1])

    @Test
    fun homeContentDark() = capture("home_content_dark", true, states[1])

    @Test
    fun homeAffixesUnavailableLight() = capture("home_affixes_unavailable_light", false, states[2])

    @Test
    fun homeAffixesUnavailableDark() = capture("home_affixes_unavailable_dark", true, states[2])
}
