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

    @Test
    fun `canHandleBackAtRoot is false on Home at its root`() {
        val shellState = state()
        composeRule.runOnIdle {
            assertFalse(shellState.canHandleBackAtRoot)
        }
    }

    @Test
    fun `canHandleBackAtRoot is true on a non-Home tab at its root`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.select(TopLevelDestination.PROFILE) }
        composeRule.runOnIdle {
            assertTrue(shellState.canHandleBackAtRoot)
        }
    }

    @Test
    fun `canHandleBackAtRoot is false on a non-Home tab with depth greater than one`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.select(TopLevelDestination.PROFILE) }
        composeRule.runOnIdle { shellState.backStackFor(TopLevelDestination.PROFILE).add(detailKey) }
        composeRule.runOnIdle {
            assertFalse(shellState.canHandleBackAtRoot)
        }
    }

    /**
     * The two properties the flattened projection exists for: nothing is dropped, and the active
     * tab's top is last. Asserted per tab because the ordering is derived from
     * `TopLevelDestination.entries`, so a fourth destination must not need this test rewritten.
     */
    @Test
    fun `the flattened stack ends with the active tab and keeps every other key`() {
        val shellState = state()
        TopLevelDestination.entries.forEach { destination ->
            composeRule.runOnIdle { shellState.select(destination) }
            composeRule.runOnIdle {
                val flattened = shellState.flattenedBackStack
                assertEquals(
                    TopLevelDestination.entries.filter { it != destination }.map { it.rootKey } +
                        destination.rootKey,
                    flattened,
                    "${destination.name} should sit last, behind the other tabs in enum order",
                )
            }
        }
    }

    @Test
    fun `the flattened stack ends with the deepest entry of the active tab`() {
        val shellState = state()
        composeRule.runOnIdle { shellState.select(TopLevelDestination.PROFILE) }
        composeRule.runOnIdle { shellState.currentBackStack.add(detailKey) }
        composeRule.runOnIdle {
            assertEquals(
                listOf(HomeKey, WeekKey, ProfileKey, detailKey),
                shellState.flattenedBackStack,
            )
        }
    }
}
