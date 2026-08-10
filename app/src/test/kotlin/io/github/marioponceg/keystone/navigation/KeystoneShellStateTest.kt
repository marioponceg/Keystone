package io.github.marioponceg.keystone.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class KeystoneShellStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val detailKey = CharacterId(
        region = Region.EU,
        realm = Realm(name = "Silvermoon", slug = "silvermoon"),
        name = "Keystone",
    ).toKey()

    private fun state(): KeystoneShellState {
        lateinit var shellState: KeystoneShellState
        composeRule.setContent { shellState = rememberKeystoneShellState() }
        composeRule.waitForIdle()
        return shellState
    }

    @Test
    fun `starts on Home with one entry per tab`() {
        val shellState = state()
        assertEquals(TopLevelDestination.HOME, shellState.selected)
        TopLevelDestination.entries.forEach { destination ->
            assertEquals(
                listOf(destination.rootKey),
                shellState.backStackFor(destination).toList(),
                "${destination.name} should start at its root",
            )
        }
    }

    @Test
    fun `selecting a tab swaps the active back stack`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.select(TopLevelDestination.WEEK) }
        composeRule.runOnIdle {
            assertEquals(TopLevelDestination.WEEK, shellState.selected)
            assertEquals(listOf(WeekKey), shellState.currentBackStack.toList())
        }
    }

    @Test
    fun `each tab keeps its own stack across a switch`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.backStackFor(TopLevelDestination.HOME).add(detailKey) }
        composeRule.runOnIdle { shellState.select(TopLevelDestination.WEEK) }
        composeRule.runOnIdle { shellState.select(TopLevelDestination.HOME) }
        composeRule.runOnIdle {
            assertEquals(
                listOf(HomeKey, detailKey),
                shellState.currentBackStack.toList(),
                "Switching away and back must not reset the Home stack",
            )
        }
    }

    @Test
    fun `reselecting the active tab pops it to its root`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.backStackFor(TopLevelDestination.HOME).add(detailKey) }
        composeRule.runOnIdle { shellState.select(TopLevelDestination.HOME) }
        composeRule.runOnIdle {
            assertEquals(listOf(HomeKey), shellState.currentBackStack.toList())
        }
    }

    @Test
    fun `back pops within the active tab first`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.backStackFor(TopLevelDestination.HOME).add(detailKey) }
        composeRule.runOnIdle {
            assertTrue(shellState.onBack())
            assertEquals(listOf(HomeKey), shellState.currentBackStack.toList())
        }
    }

    @Test
    fun `back from another tab at its root returns to Home`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.select(TopLevelDestination.PROFILE) }
        composeRule.runOnIdle {
            assertTrue(shellState.onBack())
            assertEquals(TopLevelDestination.HOME, shellState.selected)
        }
    }

    @Test
    fun `back on Home at its root is not handled so the app can exit`() {
        val shellState = state()
        composeRule.runOnIdle { assertFalse(shellState.onBack()) }
    }
}
