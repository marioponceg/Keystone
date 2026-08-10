package io.github.marioponceg.keystone.navigation

import androidx.activity.ComponentActivity
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Where Back goes once the whole shell is assembled, judged from the system's side rather than from
 * [KeystoneShellState] alone: `KeystoneShellStateTest` already covers the policy in isolation, and
 * what this file adds is whether the *shell* leaves the gesture alone when it should.
 *
 * That distinction is load-bearing. `NavDisplay` installs a back handler of its own, enabled
 * whenever the scene it computed reports previous entries, and a handler that is enabled consumes
 * the gesture whether or not anything moves. So "Back at Home's root leaves the app" cannot be
 * asserted by inspecting the back stacks — only by pressing Back and asking whether the Activity
 * finished. Run at both widths because the scene strategy differs between them.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h900dp")
class KeystoneShellBackTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun TestShell(widthDp: Float, onShellState: (KeystoneShellState) -> Unit) {
        val shellState = rememberKeystoneShellState()
        onShellState(shellState)
        val directive = calculatePaneScaffoldDirective(
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                    widthDp = widthDp,
                    heightDp = WINDOW_HEIGHT_DP,
                ),
                windowPosture = Posture(),
            ),
        ).copy(horizontalPartitionSpacerSize = 0.dp)
        KeystoneShell(
            shellState = shellState,
            directive = directive,
            windowInfo = KeystoneWindowInfo(
                isWidthAtLeastMedium = widthDp >= MEDIUM_WIDTH_DP,
                isTabletop = false,
            ),
            homePane = { FoundryText(text = "home") },
            weekPane = { FoundryText(text = "week") },
            profilePane = { FoundryText(text = "profile") },
            detailPane = { key, _ -> FoundryText(text = key.name) },
        )
    }

    private fun setShell(widthDp: Float): KeystoneShellState {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent {
            FoundryTheme { TestShell(widthDp) { shellState = it } }
        }
        composeRule.waitForIdle()
        return shellState
    }

    private fun pressBack() = composeRule.runOnIdle {
        composeRule.activity.onBackPressedDispatcher.onBackPressed()
    }

    /**
     * Home is the root of the whole app: nothing below it, so Back has to fall through to the
     * system and close the Activity. Any handler the shell leaves enabled here swallows it and the
     * app becomes impossible to leave with Back.
     */
    @Test
    fun backAtHomesRootLeavesTheApp() = assertBackAtHomesRootLeavesTheApp(COMPACT_WIDTH_DP)

    @Test
    @Config(qualifiers = "w1280dp-h900dp")
    fun backAtHomesRootLeavesTheAppAtExpandedWidth() =
        assertBackAtHomesRootLeavesTheApp(EXPANDED_WIDTH_DP)

    private fun assertBackAtHomesRootLeavesTheApp(widthDp: Float) {
        setShell(widthDp)

        composeRule.runOnIdle {
            assertFalse(
                composeRule.activity.onBackPressedDispatcher.hasEnabledCallbacks(),
                "Nothing in the shell may claim Back while Home sits at its root",
            )
        }
        pressBack()

        composeRule.runOnIdle {
            assertTrue(composeRule.activity.isFinishing, "Back at Home's root should close the app")
        }
    }

    /** The other half of the policy: any other tab at its root returns to Home instead. */
    @Test
    fun backAtAnotherTabsRootReturnsToHome() {
        val shellState = setShell(COMPACT_WIDTH_DP)

        composeRule.runOnIdle { shellState.select(TopLevelDestination.WEEK) }
        pressBack()

        composeRule.runOnIdle {
            assertEquals(TopLevelDestination.HOME, shellState.selected)
            assertFalse(composeRule.activity.isFinishing, "Back out of Week must not close the app")
        }
    }

    private companion object {
        const val COMPACT_WIDTH_DP = 411f
        const val MEDIUM_WIDTH_DP = 600f
        const val EXPANDED_WIDTH_DP = 1280f
        const val WINDOW_HEIGHT_DP = 900f
    }
}
