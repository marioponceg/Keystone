package io.github.marioponceg.keystone.ui.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.navigation.TopLevelDestination

const val TAG_NAV_BAR = "keystone_nav_bar"
const val TAG_NAV_RAIL = "keystone_nav_rail"

private fun TopLevelDestination.icon(): ImageVector = when (this) {
    TopLevelDestination.HOME -> Icons.Filled.Search
    TopLevelDestination.WEEK -> Icons.Filled.DateRange
    TopLevelDestination.PROFILE -> Icons.Filled.Person
}

/**
 * Foundry tokens onto the bar's item slots, following the mapping the design system already uses:
 * `accent` + `onAccent` for the emphasised element (`FoundryButton`'s filled style), `surface` +
 * `onSurface` for a container and its text (`FoundryCard`), `onSurfaceMuted` for anything
 * de-emphasised. The app never installs a `MaterialTheme` colour scheme, so without this every
 * unset slot falls back to M3's baseline purple.
 */
@Composable
private fun navigationBarItemColors(): NavigationBarItemColors {
    val colors = FoundryTheme.colors
    return NavigationBarItemDefaults.colors(
        selectedIconColor = colors.onAccent,
        selectedTextColor = colors.onSurface,
        indicatorColor = colors.accent,
        unselectedIconColor = colors.onSurfaceMuted,
        unselectedTextColor = colors.onSurfaceMuted,
    )
}

/** The rail's equivalent of [navigationBarItemColors]; the two must not drift apart. */
@Composable
private fun navigationRailItemColors(): NavigationRailItemColors {
    val colors = FoundryTheme.colors
    return NavigationRailItemDefaults.colors(
        selectedIconColor = colors.onAccent,
        selectedTextColor = colors.onSurface,
        indicatorColor = colors.accent,
        unselectedIconColor = colors.onSurfaceMuted,
        unselectedTextColor = colors.onSurfaceMuted,
    )
}

/**
 * The app's navigation chrome: a bottom bar on a phone, a rail from medium width upwards.
 *
 * Which container to draw is a window-level fact, so it arrives as [KeystoneWindowInfo] and never
 * touches a `UiState`.
 *
 * **On insets.** The bar and the rail apply the system inset on their own edge (Material3 does this
 * by default), and the pane then *consumes* that edge so `WindowInsets.safeDrawing` inside it —
 * which `safeDrawingContentPadding` reads — returns only what is left. Without the
 * `consumeWindowInsets` calls below, a list would add the system navigation bar's height a second
 * time underneath an app bar that already covers it.
 *
 * Content is bounded above the bar rather than scrolling behind it. That is not a retreat from
 * edge-to-edge: the rule recorded in `AGENTS.md` is about the *system* bars, which are translucent
 * and which content should still travel behind. An app-drawn navigation bar is opaque, so anything
 * behind it is simply invisible.
 *
 * Never wrap the adaptive scaffold itself in padding — it does not propagate `PaddingValues` to its
 * panes, and padding the parent clips the whole edge-to-edge layout.
 */
@Composable
fun KeystoneNavigationScaffold(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
    windowInfo: KeystoneWindowInfo = KeystoneWindowInfo.Compact,
    content: @Composable () -> Unit,
) {
    if (windowInfo.isWidthAtLeastMedium) {
        Row(modifier = Modifier.fillMaxSize()) {
            val itemColors = navigationRailItemColors()
            NavigationRail(
                modifier = Modifier.testTag(TAG_NAV_RAIL),
                containerColor = FoundryTheme.colors.surface,
                contentColor = FoundryTheme.colors.onSurface,
            ) {
                TopLevelDestination.entries.forEach { destination ->
                    NavigationRailItem(
                        selected = destination == selected,
                        onClick = { onSelect(destination) },
                        icon = { Icon(destination.icon(), contentDescription = null) },
                        label = { Text(destination.label) },
                        colors = itemColors,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Start)),
            ) {
                content()
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
            ) {
                content()
            }
            val itemColors = navigationBarItemColors()
            NavigationBar(
                modifier = Modifier.testTag(TAG_NAV_BAR),
                containerColor = FoundryTheme.colors.surface,
                contentColor = FoundryTheme.colors.onSurface,
            ) {
                TopLevelDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = destination == selected,
                        onClick = { onSelect(destination) },
                        icon = { Icon(destination.icon(), contentDescription = null) },
                        label = { Text(destination.label) },
                        colors = itemColors,
                    )
                }
            }
        }
    }
}
