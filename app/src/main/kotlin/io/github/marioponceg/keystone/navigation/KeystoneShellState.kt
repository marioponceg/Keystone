package io.github.marioponceg.keystone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

/**
 * One back stack per tab, plus which tab is showing.
 *
 * Navigation3 drives a single `NavDisplay` from a single `NavBackStack`, so multiple back stacks
 * are held here and the active one is handed over. The entry decorators in `KeystoneShell`
 * (`rememberSaveableStateHolderNavEntryDecorator`, `rememberViewModelStoreNavEntryDecorator`) key
 * off nav entries, so each tab's screen state and ViewModels survive a switch.
 *
 * **Verify tab-state retention on a device.** Robolectric captures composables in isolation and
 * cannot see this, the same structural blind spot that let the v0.4 inset bug survive three
 * versions of goldens.
 */
@Stable
class KeystoneShellState(
    private val backStacks: Map<TopLevelDestination, NavBackStack<NavKey>>,
    private val selectedState: MutableState<TopLevelDestination>,
) {
    val selected: TopLevelDestination get() = selectedState.value

    val currentBackStack: NavBackStack<NavKey> get() = backStacks.getValue(selected)

    fun backStackFor(destination: TopLevelDestination): NavBackStack<NavKey> =
        backStacks.getValue(destination)

    /**
     * Selecting the tab you are already on pops it to its root — the standard bottom-bar
     * affordance for "take me back to the top of this section".
     */
    fun select(destination: TopLevelDestination) {
        if (destination == selected) {
            val stack = backStacks.getValue(destination)
            while (stack.size > 1) {
                stack.removeLastOrNull()
            }
        } else {
            selectedState.value = destination
        }
    }

    /**
     * True when the active tab is sitting at its root and is not Home — the one case Back must be
     * intercepted here.
     *
     * Deliberately narrow. Navigation3's `NavDisplay` installs its own predictive-back handling and
     * already covers "the stack can pop", so a flag that were also true there would put two
     * handlers on the same gesture. The Back policy lives on this class rather than inline in the
     * shell so it stays in one place and stays testable.
     */
    val canHandleBackAtRoot: Boolean
        get() = currentBackStack.size == 1 && selected != TopLevelDestination.HOME

    /** Returns true when Back was consumed here. */
    fun onBack(): Boolean {
        val stack = currentBackStack
        return when {
            stack.size > 1 -> {
                stack.removeLastOrNull()
                true
            }
            selected != TopLevelDestination.HOME -> {
                selectedState.value = TopLevelDestination.HOME
                true
            }
            else -> false
        }
    }
}

@Composable
fun rememberKeystoneShellState(): KeystoneShellState {
    val home = rememberNavBackStack(HomeKey)
    val week = rememberNavBackStack(WeekKey)
    val profile = rememberNavBackStack(ProfileKey)
    // Saveable so the selected tab survives configuration change and process death; the stacks
    // themselves are already saveable through rememberNavBackStack.
    val selected = rememberSaveable { mutableStateOf(TopLevelDestination.HOME) }
    return remember(home, week, profile, selected) {
        KeystoneShellState(
            backStacks = mapOf(
                TopLevelDestination.HOME to home,
                TopLevelDestination.WEEK to week,
                TopLevelDestination.PROFILE to profile,
            ),
            selectedState = selected,
        )
    }
}
