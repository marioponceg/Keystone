package io.github.marioponceg.keystone.ui.home

import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes

data class HomeUiState(
    val affixes: AffixesUiState = AffixesUiState.Loading,
    val name: String = "",
    val realm: String = "",
    val region: Region = Region.EU,
    val recentSearches: List<RecentSearch> = emptyList(),
) {
    val canSearch: Boolean get() = name.isNotBlank() && realm.isNotBlank()
}

sealed interface AffixesUiState {
    data object Loading : AffixesUiState
    data class Content(val affixes: WeeklyAffixes) : AffixesUiState

    /** The affixes call failed; the card collapses to a quiet retry — search never blocks. */
    data object Unavailable : AffixesUiState
}

sealed interface HomeEvent {
    data class NameChanged(val value: String) : HomeEvent
    data class RealmChanged(val value: String) : HomeEvent
    data class RegionSelected(val region: Region) : HomeEvent
    data object SearchSubmitted : HomeEvent
    data class RecentSelected(val search: RecentSearch) : HomeEvent
    data class RecentRemoved(val id: CharacterId) : HomeEvent
    data object RetryAffixes : HomeEvent
}

sealed interface HomeEffect {
    data class NavigateToCharacter(val id: CharacterId) : HomeEffect
}
