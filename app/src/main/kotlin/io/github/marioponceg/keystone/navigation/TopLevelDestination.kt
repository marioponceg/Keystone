package io.github.marioponceg.keystone.navigation

import androidx.navigation3.runtime.NavKey

/**
 * The tabs of the navigation bar, in bar order.
 *
 * `HOME` is first deliberately: it is the destination Back returns to from any other tab, and the
 * one the app opens on.
 */
enum class TopLevelDestination(val label: String, val rootKey: NavKey) {
    HOME("Search", HomeKey),
    WEEK("Week", WeekKey),
    PROFILE("Profile", ProfileKey),
}
