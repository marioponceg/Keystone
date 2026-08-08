package io.github.marioponceg.keystone.ui.character

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.WithFakeImages
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Tabletop: a foldable half-open with a horizontal hinge. The score header goes above the fold and
 * the runs list below, so the top half stays readable while the device sits on a surface.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w674dp-h841dp")
class CharacterDetailTabletopScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Index 1 is the Content state: a loaded profile with score and best runs. (Index 0 is
    // Loading, which would never exercise TabletopContentState at all — see the same note in
    // KeystoneShellScreenshotTest.)
    private val contentState = CharacterDetailStateProvider().values.toList()[1]

    private fun capture(name: String, darkTheme: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                WithFakeImages {
                    CharacterDetailContent(
                        state = contentState,
                        onEvent = {},
                        onOpenRun = {},
                        showBackAction = false,
                        isTabletop = true,
                    )
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun tabletopLight() = capture("character_detail_tabletop_light", false)

    @Test
    fun tabletopDark() = capture("character_detail_tabletop_dark", true)
}
