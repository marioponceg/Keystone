package io.github.marioponceg.keystone.ui.character

import io.github.marioponceg.keystone.domain.error.KeystoneError
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.SeasonScore
import io.github.marioponceg.keystone.domain.usecase.GetCharacterProfile
import io.github.marioponceg.keystone.domain.usecase.SaveRecentSearch
import io.github.marioponceg.keystone.navigation.CharacterDetailKey
import io.github.marioponceg.keystone.ui.FakeCharacterRepository
import io.github.marioponceg.keystone.ui.FakeRecentSearchesRepository
import io.github.marioponceg.keystone.ui.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val key = CharacterDetailKey(region = "EU", realm = "Tarren Mill", name = "Gingi")
    private val id = CharacterId(Region.EU, "Tarren Mill", "Gingi")
    private val profile = CharacterProfile(
        id = id,
        name = "Gingi",
        realm = "Tarren Mill",
        characterClass = "Druid",
        spec = "Feral",
        score = SeasonScore(3500.0, "#ff8000", emptyList()),
        bestRuns = emptyList(),
    )
    private val recents = FakeRecentSearchesRepository()

    private fun viewModel(repository: FakeCharacterRepository) = CharacterDetailViewModel(
        key = key,
        getCharacterProfile = GetCharacterProfile(repository),
        saveRecentSearch = SaveRecentSearch(recents),
    )

    @Test
    fun `success shows content and saves the recent search`() = runTest {
        val vm = viewModel(FakeCharacterRepository(KeystoneResult.Success(profile)))
        advanceUntilIdle()
        assertEquals(CharacterDetailUiState.Content(profile), vm.uiState.value)
        assertEquals(listOf(id), recents.searches.first().map { it.id })
    }

    @Test
    fun `not found is its own state and saves nothing`() = runTest {
        val vm = viewModel(FakeCharacterRepository(KeystoneResult.Failure(KeystoneError.CharacterNotFound)))
        advanceUntilIdle()
        assertEquals(CharacterDetailUiState.NotFound(id), vm.uiState.value)
        assertTrue(recents.searches.first().isEmpty())
    }

    @Test
    fun `network failure is retryable and retry recovers`() = runTest {
        val repository = FakeCharacterRepository(KeystoneResult.Failure(KeystoneError.Network))
        val vm = viewModel(repository)
        advanceUntilIdle()
        assertIs<CharacterDetailUiState.Error>(vm.uiState.value)
        repository.result = KeystoneResult.Success(profile)
        vm.onEvent(CharacterDetailEvent.Retry)
        advanceUntilIdle()
        assertEquals(CharacterDetailUiState.Content(profile), vm.uiState.value)
    }
}
