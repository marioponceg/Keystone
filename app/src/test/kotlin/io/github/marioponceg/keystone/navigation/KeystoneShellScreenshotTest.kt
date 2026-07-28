package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.window.core.layout.WindowSizeClass
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.character.CharacterDetailContent
import io.github.marioponceg.keystone.ui.character.CharacterDetailStateProvider
import io.github.marioponceg.keystone.ui.home.HomeContent
import io.github.marioponceg.keystone.ui.home.HomeStateProvider
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w1280dp-h900dp")
class KeystoneShellScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val homeContentState = HomeStateProvider().values.toList()[1]

    // Index 1 is the Content state: a loaded profile with score and best runs. (Index 0 is
    // Loading — verified against CharacterDetailStateProvider's declared value order.)
    private val detailContentState = CharacterDetailStateProvider().values.toList()[1]

    private val selectedKey = CharacterDetailKey(
        region = "EU",
        realmSlug = "silvermoon",
        realmName = "Silvermoon",
        name = "Keystone",
    )

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun TestShell(withCharacter: Boolean, tabletop: Boolean = false) {
        val backStack = rememberNavBackStack(HomeKey)
        if (withCharacter && backStack.size == 1) {
            backStack.add(selectedKey)
        }
        val directive = calculatePaneScaffoldDirective(
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.compute(1280f, 900f),
                windowPosture = Posture(isTabletop = tabletop),
            ),
        ).copy(horizontalPartitionSpacerSize = 0.dp)
        KeystoneShell(
            backStack = backStack,
            directive = directive,
            homePane = { HomeContent(state = homeContentState, onEvent = {}) },
            detailPane = { _, _ ->
                CharacterDetailContent(
                    state = detailContentState,
                    onEvent = {},
                    showBackAction = false,
                )
            },
        )
    }

    private fun capture(name: String, darkTheme: Boolean, withCharacter: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                TestShell(withCharacter = withCharacter)
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun shellWithCharacterLight() = capture("shell_detail_light", false, withCharacter = true)

    @Test
    fun shellWithCharacterDark() = capture("shell_detail_dark", true, withCharacter = true)

    @Test
    fun shellPlaceholderLight() = capture("shell_placeholder_light", false, withCharacter = false)

    @Test
    fun shellPlaceholderDark() = capture("shell_placeholder_dark", true, withCharacter = false)
}
