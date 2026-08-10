package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import io.github.marioponceg.foundry.components.FoundryButton
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * Behaviour of the wiring [KeystoneShell] does itself, as opposed to how it looks: the screenshot
 * tests all pass pane content that throws its navigation callbacks away, so `toKey()` and the
 * dedupe guard around `backStack.add` — the only non-trivial logic in the navigation layer — were
 * never executed by a test.
 *
 * Run at expanded width on purpose. Two panes mean the home pane stays on screen after the first
 * navigation, which is both what makes the second click possible here and the situation where a
 * real user most easily triggers it: on a two-pane window the recents list they just tapped is
 * still right there.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w1280dp-h900dp")
class KeystoneShellNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val characterId = CharacterId(
        region = Region.EU,
        realm = Realm(name = "Silvermoon", slug = "silvermoon"),
        name = "Keystone",
    )

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun TestShell(onShellState: (KeystoneShellState) -> Unit) {
        val shellState = rememberKeystoneShellState()
        onShellState(shellState)
        val directive = calculatePaneScaffoldDirective(
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                    widthDp = 1280f,
                    heightDp = 900f,
                ),
                windowPosture = Posture(),
            ),
        ).copy(horizontalPartitionSpacerSize = 0.dp)
        KeystoneShell(
            shellState = shellState,
            directive = directive,
            windowInfo = KeystoneWindowInfo(isWidthAtLeastMedium = true, isTabletop = false),
            homePane = { onNavigateToCharacter ->
                FoundryButton(
                    text = "Open character",
                    onClick = { onNavigateToCharacter(characterId) },
                    modifier = Modifier.testTag(TAG_OPEN),
                )
            },
            weekPane = { FoundryText(text = "week") },
            profilePane = { FoundryText(text = "profile") },
            detailPane = { key, _ -> FoundryText(text = key.name) },
        )
    }

    @Test
    fun navigatingToACharacterPushesTheKeyDerivedFromItsId() {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent {
            FoundryTheme { TestShell(onShellState = { shellState = it }) }
        }

        composeRule.onNodeWithTag(TAG_OPEN).performClick()

        assertEquals(listOf(HomeKey, characterId.toKey()), shellState.currentBackStack.toList())
    }

    /**
     * Opening the character already on top must be a no-op, not a second identical entry — three
     * entries would mean a user who double-taps a recent search has to press Back twice to get out
     * of a screen they only opened once.
     */
    @Test
    fun openingTheSameCharacterTwiceDoesNotStackADuplicateEntry() {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent {
            FoundryTheme { TestShell(onShellState = { shellState = it }) }
        }

        composeRule.onNodeWithTag(TAG_OPEN).performClick()
        composeRule.onNodeWithTag(TAG_OPEN).performClick()

        assertEquals(
            listOf(HomeKey, characterId.toKey()),
            shellState.currentBackStack.toList(),
            "The dedupe guard should keep the back stack at Home + one detail entry",
        )
    }

    /**
     * Profile pushes onto the Profile stack, never onto Home's. Getting this wrong would put a
     * character detail behind the wrong tab and make Back leave the section the user was in.
     */
    @Test
    fun openingACharacterFromProfilePushesOntoTheProfileStack() {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent {
            FoundryTheme { TestShell(onShellState = { shellState = it }) }
        }

        composeRule.runOnIdle { shellState.select(TopLevelDestination.PROFILE) }
        composeRule.runOnIdle { shellState.currentBackStack.add(characterId.toKey()) }

        composeRule.runOnIdle {
            assertEquals(
                listOf(ProfileKey, characterId.toKey()),
                shellState.backStackFor(TopLevelDestination.PROFILE).toList(),
            )
            assertEquals(
                listOf(HomeKey),
                shellState.backStackFor(TopLevelDestination.HOME).toList(),
                "The Home stack must be untouched",
            )
        }
    }

    private companion object {
        const val TAG_OPEN = "test_open_character"
    }
}
