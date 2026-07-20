package io.github.marioponceg.keystone.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.marioponceg.keystone.domain.error.KeystoneError
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.usecase.GetCharacterProfile
import io.github.marioponceg.keystone.domain.usecase.SaveRecentSearch
import io.github.marioponceg.keystone.navigation.CharacterDetailKey
import io.github.marioponceg.keystone.navigation.toCharacterId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CharacterDetailViewModel.Factory::class)
class CharacterDetailViewModel @AssistedInject constructor(
    @Assisted key: CharacterDetailKey,
    private val getCharacterProfile: GetCharacterProfile,
    private val saveRecentSearch: SaveRecentSearch,
) : ViewModel() {

    private val id = key.toCharacterId()

    private val _uiState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onEvent(event: CharacterDetailEvent) {
        when (event) {
            CharacterDetailEvent.Retry -> load()
            CharacterDetailEvent.Back -> Unit // handled by the screen's onBack callback
        }
    }

    private fun load() {
        _uiState.value = CharacterDetailUiState.Loading
        viewModelScope.launch {
            when (val result = getCharacterProfile(id)) {
                is KeystoneResult.Success -> {
                    // Only resolved characters become recents — typos never pollute the list.
                    saveRecentSearch(RecentSearch(id, searchedAtEpochMillis = System.currentTimeMillis()))
                    _uiState.value = CharacterDetailUiState.Content(result.value)
                }
                is KeystoneResult.Failure -> _uiState.value = when (result.error) {
                    KeystoneError.CharacterNotFound -> CharacterDetailUiState.NotFound(id)
                    KeystoneError.Network, KeystoneError.Unknown -> CharacterDetailUiState.Error(id)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: CharacterDetailKey): CharacterDetailViewModel
    }
}
