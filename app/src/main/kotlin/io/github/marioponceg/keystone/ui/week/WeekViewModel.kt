package io.github.marioponceg.keystone.ui.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.usecase.GetWeeklyAffixes
import io.github.marioponceg.keystone.domain.usecase.ObserveSelectedRegion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the weekly affixes, which until v0.4 were loaded by `HomeViewModel`.
 *
 * Created by the nav entry decorator the first time the Week tab is shown and retained afterwards,
 * so the call happens once per process rather than on every launch. This is a deliberate behaviour
 * change from v0.4, where the affixes were fetched at start-up because Home displayed them.
 */
@HiltViewModel
class WeekViewModel @Inject constructor(
    private val getWeeklyAffixes: GetWeeklyAffixes,
    private val observeSelectedRegion: ObserveSelectedRegion,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeekUiState>(WeekUiState.Loading)
    val uiState: StateFlow<WeekUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { load() }
    }

    fun onEvent(event: WeekEvent) {
        when (event) {
            is WeekEvent.Retry -> viewModelScope.launch {
                _uiState.update { WeekUiState.Loading }
                load()
            }
        }
    }

    /**
     * The region is re-read on every load rather than cached: Home writes it through
     * `SaveSelectedRegion`, and a user who switches region there must see that week's affixes when
     * they come back to this tab.
     */
    private suspend fun load() {
        val region: Region = observeSelectedRegion().first()
        val state = when (val result = getWeeklyAffixes(region)) {
            is KeystoneResult.Success -> WeekUiState.Content(result.value)
            is KeystoneResult.Failure -> WeekUiState.Unavailable
        }
        _uiState.update { state }
    }
}
