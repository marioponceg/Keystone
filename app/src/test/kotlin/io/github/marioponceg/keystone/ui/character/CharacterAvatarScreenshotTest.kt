package io.github.marioponceg.keystone.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.WithFakeImages
import io.github.marioponceg.keystone.ui.common.CharacterAvatar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h400dp")
class CharacterAvatarScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun capture(name: String, darkTheme: Boolean, url: String?, characterClass: String) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                WithFakeImages {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CharacterAvatar(
                            url = url,
                            characterClass = characterClass,
                            characterName = "Zoyu",
                        )
                    }
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun avatarLoadedLight() =
        capture("avatar_loaded_light", false, "https://example.invalid/a.jpg", "Demon Hunter")

    @Test
    fun avatarLoadedDark() =
        capture("avatar_loaded_dark", true, "https://example.invalid/a.jpg", "Demon Hunter")

    @Test
    fun avatarMissingUrlLight() = capture("avatar_missing_light", false, null, "Demon Hunter")

    @Test
    fun avatarMissingUrlDark() = capture("avatar_missing_dark", true, null, "Demon Hunter")

    @Test
    fun avatarMissingUrlUnknownClass() =
        capture("avatar_missing_unknown_class", false, null, "Tinkerer")
}
