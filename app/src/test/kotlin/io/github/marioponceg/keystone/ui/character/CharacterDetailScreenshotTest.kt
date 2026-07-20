package io.github.marioponceg.keystone.ui.character

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
// Tall viewport so the content state (header + score card + best runs) fits in one capture and
// the goldens actually guard the whole screen, not just what fits a default phone height.
@Config(sdk = [35], qualifiers = "w411dp-h1200dp")
class CharacterDetailScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val states = CharacterDetailStateProvider().values.toList()

    private fun capture(name: String, darkTheme: Boolean, state: CharacterDetailUiState) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                CharacterDetailContent(state = state, onEvent = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun characterDetailLoadingLight() = capture("character_detail_loading_light", false, states[0])

    @Test
    fun characterDetailLoadingDark() = capture("character_detail_loading_dark", true, states[0])

    @Test
    fun characterDetailContentLight() = capture("character_detail_content_light", false, states[1])

    @Test
    fun characterDetailContentDark() = capture("character_detail_content_dark", true, states[1])

    @Test
    fun characterDetailNotFoundLight() = capture("character_detail_not_found_light", false, states[2])

    @Test
    fun characterDetailNotFoundDark() = capture("character_detail_not_found_dark", true, states[2])

    @Test
    fun characterDetailErrorLight() = capture("character_detail_error_light", false, states[3])

    @Test
    fun characterDetailErrorDark() = capture("character_detail_error_dark", true, states[3])
}
