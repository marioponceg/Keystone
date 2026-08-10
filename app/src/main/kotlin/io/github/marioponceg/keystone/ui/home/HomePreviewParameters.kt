@file:Suppress("MatchingDeclarationName", "Filename")

package io.github.marioponceg.keystone.ui.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region

// Filename follows Task 8's "HomePreviewParameters.kt" spec (shared preview data), rather than
// the single-class convention `MatchingDeclarationName`/`Filename` expects.
class HomeStateProvider : PreviewParameterProvider<HomeUiState> {
    override val values = sequenceOf(
        HomeUiState(),
        HomeUiState(
            name = "Gingi",
            selectedRealm = Realm("Tarren Mill", "tarren-mill"),
            recentSearches = listOf(
                RecentSearch(CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Gingi"), 1),
                RecentSearch(CharacterId(Region.US, Realm("Illidan", "illidan"), "Dorki"), 2),
            ),
        ),
    )
}

/** Query + result-list pairs for [RealmPickerContent] previews and screenshot tests. */
class RealmPickerStateProvider : PreviewParameterProvider<Pair<String, List<Realm>>> {
    private val realms = listOf(
        Realm("Aggra", "aggra"),
        Realm("Tarren Mill", "tarren-mill"),
        Realm("Zul'jin", "zuljin"),
    )
    override val values = sequenceOf(
        "" to realms,
        "tar" to realms.filter { it.name.contains("tar", true) },
        "zzz" to emptyList(),
    )
}
