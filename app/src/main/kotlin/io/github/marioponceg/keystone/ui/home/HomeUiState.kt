package io.github.marioponceg.keystone.ui.home

import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes

data class HomeUiState(
    val affixes: AffixesUiState = AffixesUiState.Loading,
    val name: String = "",
    val selectedRealm: Realm? = null,
    val region: Region = Region.EU,
    val recentSearches: List<RecentSearch> = emptyList(),
    val isRealmSheetVisible: Boolean = false,
    val realmQuery: String = "",
    val realmResults: List<Realm> = emptyList(),
) {
    val canSearch: Boolean get() = name.isNotBlank() && selectedRealm != null
}

sealed interface AffixesUiState {
    data object Loading : AffixesUiState
    data class Content(val affixes: WeeklyAffixes) : AffixesUiState

    /** The affixes call failed; the card collapses to a quiet retry — search never blocks. */
    data object Unavailable : AffixesUiState
}

sealed interface HomeEvent {
    data class NameChanged(val value: String) : HomeEvent
    data object RealmFieldTapped : HomeEvent
    data class RealmQueryChanged(val value: String) : HomeEvent
    data class RealmSelected(val realm: Realm) : HomeEvent
    data object RealmSheetDismissed : HomeEvent
    data class RegionSelected(val region: Region) : HomeEvent
    data object SearchSubmitted : HomeEvent
    data class RecentSelected(val search: RecentSearch) : HomeEvent
    data class RecentRemoved(val id: CharacterId) : HomeEvent
    data object RetryAffixes : HomeEvent
}

sealed interface HomeEffect {
    data class NavigateToCharacter(val id: CharacterId) : HomeEffect
}
