package io.github.marioponceg.keystone.ui.profile

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
class ProfileScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val states = ProfileStateProvider().values.toList()

    private fun capture(name: String, darkTheme: Boolean, state: ProfileUiState) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                ProfileContent(state = state, onCharacterSelected = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun profileEmptyLight() = capture("profile_empty_light", false, states[0])

    @Test
    fun profileEmptyDark() = capture("profile_empty_dark", true, states[0])

    @Test
    fun profileContentLight() = capture("profile_content_light", false, states[1])

    @Test
    fun profileContentDark() = capture("profile_content_dark", true, states[1])
}
