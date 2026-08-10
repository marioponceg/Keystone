package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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

/**
 * What the two-pane layout actually shows once `NavDisplay` is handed every tab's keys.
 *
 * `ListDetailSceneStrategy` builds its scene by walking the list backwards from the end, so it now
 * meets entries belonging to other tabs — including a second `listPane`-tagged one, since `HomeKey`
 * and `ProfileKey` are both list panes. It stops at the first entry with no pane metadata, which
 * `WeekKey` is, and it fills each pane from the *last* entry holding that role. The two facts
 * together are why the split stays right; this file is the evidence rather than the argument.
 *
 * Expanded width throughout: at compact width there is only ever one pane and nothing to get wrong.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w1280dp-h900dp")
class KeystoneShellListDetailTest {

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
                    widthDp = EXPANDED_WIDTH_DP,
                    heightDp = WINDOW_HEIGHT_DP,
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
                    text = "home",
                    onClick = { onNavigateToCharacter(characterId) },
                    modifier = Modifier.testTag(TAG_HOME),
                )
            },
            weekPane = { FoundryText(text = "week", modifier = Modifier.testTag(TAG_WEEK)) },
            profilePane = { onNavigateToCharacter ->
                FoundryButton(
                    text = "profile",
                    onClick = { onNavigateToCharacter(characterId) },
                    modifier = Modifier.testTag(TAG_PROFILE),
                )
            },
            detailPane = { key, _ ->
                FoundryText(text = key.name, modifier = Modifier.testTag(TAG_DETAIL))
            },
        )
    }

    private fun setShell(): () -> KeystoneShellState {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent {
            FoundryTheme { TestShell(onShellState = { shellState = it }) }
        }
        return { shellState }
    }

    @Test
    fun homeAtItsRootShowsTheHomeListBesideThePlaceholder() {
        setShell()

        composeRule.onNodeWithTag(TAG_HOME).assertIsDisplayed()
        composeRule.onNodeWithText(PLACEHOLDER_TEXT).assertIsDisplayed()
        composeRule.onNodeWithTag(TAG_PROFILE).assertDoesNotExist()
        composeRule.onNodeWithTag(TAG_WEEK).assertDoesNotExist()
    }

    @Test
    fun homesDetailOpensBesideHomesList() {
        setShell()

        composeRule.onNodeWithTag(TAG_HOME).performClick()

        composeRule.onNodeWithTag(TAG_HOME).assertIsDisplayed()
        composeRule.onNodeWithTag(TAG_DETAIL).assertIsDisplayed()
        composeRule.onNodeWithTag(TAG_PROFILE).assertDoesNotExist()
    }

    /**
     * Week is the tab with no pane metadata at all. It must fall back to a single pane rather than
     * borrow a list or a detail from a neighbouring tab's keys.
     */
    @Test
    fun weekShowsOneWholePaneAndNeverASplit() {
        val shellState = setShell()

        composeRule.runOnIdle { shellState().select(TopLevelDestination.WEEK) }

        composeRule.onNodeWithTag(TAG_WEEK).assertIsDisplayed()
        composeRule.onNodeWithTag(TAG_HOME).assertDoesNotExist()
        composeRule.onNodeWithTag(TAG_PROFILE).assertDoesNotExist()
        composeRule.onNodeWithText(PLACEHOLDER_TEXT).assertDoesNotExist()
        composeRule.onNodeWithTag(TAG_DETAIL).assertDoesNotExist()
    }

    @Test
    fun profileAtItsRootShowsTheProfileListBesideThePlaceholder() {
        val shellState = setShell()

        composeRule.runOnIdle { shellState().select(TopLevelDestination.PROFILE) }

        composeRule.onNodeWithTag(TAG_PROFILE).assertIsDisplayed()
        composeRule.onNodeWithText(PLACEHOLDER_TEXT).assertIsDisplayed()
        composeRule.onNodeWithTag(TAG_HOME).assertDoesNotExist()
    }

    /** The named risk, stated as an assertion: Profile's detail beside Profile's list, not Home's. */
    @Test
    fun profilesDetailOpensBesideProfilesList() {
        val shellState = setShell()

        composeRule.runOnIdle { shellState().select(TopLevelDestination.PROFILE) }
        composeRule.onNodeWithTag(TAG_PROFILE).performClick()

        composeRule.onNodeWithTag(TAG_PROFILE).assertIsDisplayed()
        composeRule.onNodeWithTag(TAG_DETAIL).assertIsDisplayed()
        composeRule.onNodeWithTag(TAG_HOME).assertDoesNotExist()
    }

    private companion object {
        const val TAG_HOME = "test_ld_home"
        const val TAG_WEEK = "test_ld_week"
        const val TAG_PROFILE = "test_ld_profile"
        const val TAG_DETAIL = "test_ld_detail"

        // The detail placeholder is wired inside KeystoneShell itself, so it carries no test tag
        // of ours; its title is the only handle on it.
        const val PLACEHOLDER_TEXT = "Search for a character"
        const val EXPANDED_WIDTH_DP = 1280f
        const val WINDOW_HEIGHT_DP = 900f
    }
}
