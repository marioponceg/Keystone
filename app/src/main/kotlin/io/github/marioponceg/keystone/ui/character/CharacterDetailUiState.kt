package io.github.marioponceg.keystone.ui.character

import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Content(val profile: CharacterProfile) : CharacterDetailUiState

    /** Raider.IO does not know this character; shows the searched id and a back action. */
    data class NotFound(val id: CharacterId) : CharacterDetailUiState

    /** Network/unknown failure; retryable. */
    data class Error(val id: CharacterId) : CharacterDetailUiState
}

sealed interface CharacterDetailEvent {
    data object Retry : CharacterDetailEvent
    data object Back : CharacterDetailEvent
}
