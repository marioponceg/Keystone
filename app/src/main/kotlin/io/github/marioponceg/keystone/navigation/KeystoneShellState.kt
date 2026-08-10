package io.github.marioponceg.keystone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

/**
 * One back stack per tab, plus which tab is showing.
 *
 * Navigation3 hangs two separate things off a list of keys, and `KeystoneShell` gives each of them
 * a different list — which is the whole reason [flattenedBackStack] exists alongside
 * [currentBackStack].
 *
 * **Entry lifetime** follows the list handed to `rememberDecoratedNavEntries`. An entry is disposed
 * when its key leaves that list, and disposal invokes every decorator's `onPop` —
 * `SaveableStateHolder.removeState` and `ViewModelStore.clear` for the two the shell installs. So
 * that call gets [flattenedBackStack], and a tab switch drops nothing. Decorating only the active
 * tab's keys is what used to wipe every other tab on every switch: Home → Week erased what was
 * under `HomeKey`, and Week → Home → Week rebuilt `WeekViewModel` and re-fetched the affixes.
 *
 * **Back** follows the list handed to `NavDisplay`, which enables a back handler of its own
 * whenever the scene it computes reports previous entries. So that gets the active tab's slice
 * only. Given the flattened list instead, the foreign keys ahead of the active tail make
 * `Scene.previousEntries` permanently non-empty, and Back at Home's root stops leaving the app.
 *
 * `KeystoneShellRetentionTest` and `KeystoneShellBackTest` pin one side each; neither passes if the
 * two lists are collapsed back into one.
 *
 * **Verify tab behaviour on a device too.** Robolectric captures composables in isolation, the same
 * structural blind spot that let the v0.4 inset bug survive three versions of goldens.
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
     * Every tab's stack concatenated, **the active tab's last** — Navigation3's documented shape
     * for multiple back stacks, and what `KeystoneShell` decorates so no tab is ever disposed.
     *
     * The active tab going last is not cosmetic: it makes the active tab's stack the *tail* of this
     * list, which is what lets the shell slice the entries it displays straight off the end. The
     * order of the inactive tabs comes from `TopLevelDestination.entries` rather than being written
     * out, so a fourth destination joins it without anyone remembering to.
     *
     * A projection, never a thing to mutate — it is rebuilt from the per-tab stacks on every read.
     * Pushing goes through [currentBackStack] and popping through [onBack], both of which act on
     * the tab the user is actually in.
     */
    val flattenedBackStack: List<NavKey> get() = flattened.value

    private val flattened = derivedStateOf {
        buildList {
            TopLevelDestination.entries.forEach { destination ->
                if (destination != selected) addAll(backStacks.getValue(destination))
            }
            addAll(currentBackStack)
        }
    }

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
