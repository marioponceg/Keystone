package io.github.marioponceg.keystone.ui.week

import io.github.marioponceg.keystone.domain.model.WeeklyAffixes

/**
 * The Week tab's whole state. This is the `AffixesUiState` that used to live inside `HomeUiState`,
 * promoted to a screen state of its own: the affixes are no longer a card competing with a search
 * form, they are the screen.
 */
sealed interface WeekUiState {
    data object Loading : WeekUiState
    data class Content(val affixes: WeeklyAffixes) : WeekUiState

    /** The affixes call failed; the screen offers a quiet retry. */
    data object Unavailable : WeekUiState
}

sealed interface WeekEvent {
    data object Retry : WeekEvent
}
