package io.github.marioponceg.keystone.ui.profile

import io.github.marioponceg.keystone.domain.model.CharacterId

/**
 * Pinned characters, or nothing pinned yet.
 *
 * A list rather than a single "main character" on purpose: it keeps the list→detail pattern the
 * rest of the app uses, and v0.6 can swap local storage for the Battle.net account without
 * rewriting this screen.
 */
sealed interface ProfileUiState {
    data object Empty : ProfileUiState
    data class Content(val characters: List<CharacterId>) : ProfileUiState
}
