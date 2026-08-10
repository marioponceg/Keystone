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
 * Navigation3 drives a single `NavDisplay` from a single list of keys, so the stacks are held here
 * and the active one is handed over.
 *
 * **Known defect: a tab switch destroys the other tabs' state.** `NavDisplay` disposes any entry
 * whose `contentKey` is missing from the list it was last given, and disposal invokes every entry
 * decorator's `onPop` — `SaveableStateHolder.removeState` and `ViewModelStore.clear` for the two
 * decorators `KeystoneShell` installs. Handing over only the active tab's stack therefore erases
 * every other tab on every switch: Home → Week wipes what was under `HomeKey`, and Week → Home →
 * Week rebuilds `WeekViewModel` and re-fetches the affixes. `KeystoneShellRetentionTest` states the
 * behaviour that is wanted and is `@Ignore`d until it holds.
 *
 * The documented remedy — hand `NavDisplay` every tab's keys concatenated, active tab last — fixes
 * retention and leaves the list-detail split correct (`KeystoneShellListDetailTest` covers all
 * three tabs), but breaks Back: `NavDisplay` enables its own back handler whenever
 * `Scene.previousEntries` is non-empty, and the foreign keys ahead of the active tail always make
 * it non-empty, so Back at Home's root stops leaving the app. `KeystoneShellBackTest` is the
 * tripwire. Resolving that is a design decision for the maintainer, not something to route around.
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
     * for multiple back stacks, and the remedy the class KDoc describes.
     *
     * Not wired into [KeystoneShell] yet, and deliberately kept as the one place that spelling
     * lives so the blocked fix is a one-line change rather than a rediscovery. The order of the
     * inactive tabs comes from `TopLevelDestination.entries` rather than being written out, so a
     * fourth destination joins it without anyone remembering to. It is a projection for rendering,
     * never a thing to mutate: pushing goes through [currentBackStack] and popping through
     * [onBack], both of which act on the tab the user is actually in.
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
