package io.github.marioponceg.keystone.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.usecase.GetWeeklyAffixes
import io.github.marioponceg.keystone.domain.usecase.ObserveRecentSearches
import io.github.marioponceg.keystone.domain.usecase.ObserveSelectedRegion
import io.github.marioponceg.keystone.domain.usecase.RemoveRecentSearch
import io.github.marioponceg.keystone.domain.usecase.SaveSelectedRegion
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeeklyAffixes: GetWeeklyAffixes,
    observeRecentSearches: ObserveRecentSearches,
    private val removeRecentSearch: RemoveRecentSearch,
    observeSelectedRegion: ObserveSelectedRegion,
    private val saveSelectedRegion: SaveSelectedRegion,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects: Flow<HomeEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val region = observeSelectedRegion().first()
            _uiState.update { it.copy(region = region) }
            loadAffixes(region)
        }
        viewModelScope.launch {
            observeRecentSearches().collect { recents ->
                _uiState.update { it.copy(recentSearches = recents) }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NameChanged -> _uiState.update { it.copy(name = event.value) }
            is HomeEvent.RealmChanged -> _uiState.update { it.copy(realm = event.value) }
            is HomeEvent.RegionSelected -> onRegionSelected(event.region)
            is HomeEvent.SearchSubmitted -> onSearchSubmitted()
            is HomeEvent.RecentSelected -> _effects.trySend(HomeEffect.NavigateToCharacter(event.search.id))
            is HomeEvent.RecentRemoved -> viewModelScope.launch { removeRecentSearch(event.id) }
            is HomeEvent.RetryAffixes -> viewModelScope.launch { loadAffixes(uiState.value.region) }
        }
    }

    private fun onRegionSelected(region: Region) {
        _uiState.update { it.copy(region = region) }
        viewModelScope.launch {
            saveSelectedRegion(region)
            loadAffixes(region)
        }
    }

    private fun onSearchSubmitted() {
        val state = uiState.value
        if (!state.canSearch) return
        _effects.trySend(
            HomeEffect.NavigateToCharacter(
                CharacterId(state.region, state.realm.trim(), state.name.trim()),
            ),
        )
    }

    private suspend fun loadAffixes(region: Region) {
        _uiState.update { it.copy(affixes = AffixesUiState.Loading) }
        val affixes = when (val result = getWeeklyAffixes(region)) {
            is KeystoneResult.Success -> AffixesUiState.Content(result.value)
            is KeystoneResult.Failure -> AffixesUiState.Unavailable
        }
        _uiState.update { it.copy(affixes = affixes) }
    }
}
