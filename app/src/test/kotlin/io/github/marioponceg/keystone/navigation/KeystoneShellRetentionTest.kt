package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import io.github.marioponceg.foundry.components.FoundryButton
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The point of the per-tab back stacks: a tab you leave and come back to is where you left it.
 *
 * What makes it work is where the decoration happens. An entry is disposed — and disposal runs each
 * decorator's `onPop`, here `SaveableStateHolder.removeState` and `ViewModelStore.clear` — when its
 * key leaves the list handed to `rememberDecoratedNavEntries`. `KeystoneShell` hands that call
 * every tab's keys, so a switch drops nothing, and hands `NavDisplay` only the active tab's slice,
 * which is what keeps Back correct ([KeystoneShellBackTest]). Decorate the active tab's keys alone
 * and both tests below go red.
 *
 * Run at compact width so nothing here depends on the list-detail pane split; the scene strategy
 * gets its own coverage in [KeystoneShellListDetailTest].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h900dp")
class KeystoneShellRetentionTest {

    @get:Rule
    val composeRule = createComposeRule()

    /**
     * A pane whose only content is state that must survive a tab switch: a `rememberSaveable`
     * counter, which is exactly what `SaveableStateHolder.removeState` throws away.
     */
    @Composable
    private fun CountingPane(tag: String) {
        var count by rememberSaveable { mutableIntStateOf(0) }
        FoundryButton(
            text = "$tag=$count",
            onClick = { count++ },
            modifier = Modifier.testTag(tag),
        )
    }

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun TestShell(onShellState: (KeystoneShellState) -> Unit) {
        val shellState = rememberKeystoneShellState()
        onShellState(shellState)
        val directive = calculatePaneScaffoldDirective(
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                    widthDp = COMPACT_WIDTH_DP,
                    heightDp = WINDOW_HEIGHT_DP,
                ),
                windowPosture = Posture(),
            ),
        ).copy(horizontalPartitionSpacerSize = 0.dp)
        KeystoneShell(
            shellState = shellState,
            directive = directive,
            windowInfo = KeystoneWindowInfo(isWidthAtLeastMedium = false, isTabletop = false),
            homePane = { CountingPane(TAG_HOME_COUNTER) },
            weekPane = { CountingPane(TAG_WEEK_COUNTER) },
            profilePane = { FoundryText(text = "profile") },
            detailPane = { key, _ -> FoundryText(text = key.name) },
        )
    }

    @Test
    fun leavingATabAndComingBackKeepsItsScreenState() {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent {
            FoundryTheme { TestShell(onShellState = { shellState = it }) }
        }

        composeRule.onNodeWithTag(TAG_HOME_COUNTER).performClick()
        composeRule.onNodeWithTag(TAG_HOME_COUNTER).performClick()
        composeRule.onNodeWithTag(TAG_HOME_COUNTER).assertTextEquals("$TAG_HOME_COUNTER=2")

        composeRule.runOnIdle { shellState.select(TopLevelDestination.WEEK) }
        composeRule.runOnIdle { shellState.select(TopLevelDestination.HOME) }

        // Hand NavDisplay only the active tab's stack and this reads 0 instead: HomeKey left the
        // list while Week was showing, so the decorator dropped everything saved under it.
        composeRule.onNodeWithTag(TAG_HOME_COUNTER).assertTextEquals("$TAG_HOME_COUNTER=2")
    }

    /**
     * The other direction, and the one a user hits hardest: Week owns a `WeekViewModel` that
     * fetches the affixes, so a Week entry rebuilt on every visit means a request on every visit.
     */
    @Test
    fun aTabVisitedTwiceIsNotRebuiltTheSecondTime() {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent {
            FoundryTheme { TestShell(onShellState = { shellState = it }) }
        }

        composeRule.runOnIdle { shellState.select(TopLevelDestination.WEEK) }
        composeRule.onNodeWithTag(TAG_WEEK_COUNTER).performClick()
        composeRule.runOnIdle { shellState.select(TopLevelDestination.HOME) }
        composeRule.runOnIdle { shellState.select(TopLevelDestination.WEEK) }

        composeRule.onNodeWithTag(TAG_WEEK_COUNTER).assertTextEquals("$TAG_WEEK_COUNTER=1")
    }

    private companion object {
        const val TAG_HOME_COUNTER = "test_home_counter"
        const val TAG_WEEK_COUNTER = "test_week_counter"
        const val COMPACT_WIDTH_DP = 411f
        const val WINDOW_HEIGHT_DP = 900f
    }
}
