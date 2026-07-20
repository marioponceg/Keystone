package io.github.marioponceg.keystone.ui.home

import app.cash.turbine.test
import io.github.marioponceg.keystone.domain.error.KeystoneError
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.Affix
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes
import io.github.marioponceg.keystone.domain.usecase.GetWeeklyAffixes
import io.github.marioponceg.keystone.domain.usecase.ObserveRecentSearches
import io.github.marioponceg.keystone.domain.usecase.ObserveSelectedRegion
import io.github.marioponceg.keystone.domain.usecase.RemoveRecentSearch
import io.github.marioponceg.keystone.domain.usecase.SaveSelectedRegion
import io.github.marioponceg.keystone.ui.FakeAffixesRepository
import io.github.marioponceg.keystone.ui.FakeRecentSearchesRepository
import io.github.marioponceg.keystone.ui.FakeRegionPreferenceRepository
import io.github.marioponceg.keystone.ui.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val weekly = WeeklyAffixes("Tyrannical week", listOf(Affix("Tyrannical", "Bosses hit harder")))
    private val affixesRepository = FakeAffixesRepository(KeystoneResult.Success(weekly))
    private val recentsRepository = FakeRecentSearchesRepository()
    private val regionRepository = FakeRegionPreferenceRepository()

    private fun viewModel() = HomeViewModel(
        getWeeklyAffixes = GetWeeklyAffixes(affixesRepository),
        observeRecentSearches = ObserveRecentSearches(recentsRepository),
        removeRecentSearch = RemoveRecentSearch(recentsRepository),
        observeSelectedRegion = ObserveSelectedRegion(regionRepository),
        saveSelectedRegion = SaveSelectedRegion(regionRepository),
    )

    @Test
    fun `loads affixes for the persisted region on start`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        assertEquals(AffixesUiState.Content(weekly), vm.uiState.value.affixes)
        assertEquals(Region.EU, affixesRepository.lastRegion)
    }

    @Test
    fun `affixes failure hides the card without blocking search`() = runTest {
        affixesRepository.result = KeystoneResult.Failure(KeystoneError.Network)
        val vm = viewModel()
        advanceUntilIdle()
        assertIs<AffixesUiState.Unavailable>(vm.uiState.value.affixes)
        vm.onEvent(HomeEvent.NameChanged("Thrall"))
        vm.onEvent(HomeEvent.RealmChanged("Tarren Mill"))
        assertEquals(true, vm.uiState.value.canSearch)
    }

    @Test
    fun `retry reloads affixes`() = runTest {
        affixesRepository.result = KeystoneResult.Failure(KeystoneError.Network)
        val vm = viewModel()
        advanceUntilIdle()
        affixesRepository.result = KeystoneResult.Success(weekly)
        vm.onEvent(HomeEvent.RetryAffixes)
        advanceUntilIdle()
        assertEquals(AffixesUiState.Content(weekly), vm.uiState.value.affixes)
    }

    @Test
    fun `region change persists and reloads affixes`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.RegionSelected(Region.US))
        advanceUntilIdle()
        assertEquals(Region.US, regionRepository.region.value)
        assertEquals(Region.US, affixesRepository.lastRegion)
    }

    @Test
    fun `search submit emits navigation effect with trimmed input`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.NameChanged(" Thrall "))
        vm.onEvent(HomeEvent.RealmChanged("Tarren Mill"))
        vm.effects.test {
            vm.onEvent(HomeEvent.SearchSubmitted)
            assertEquals(
                HomeEffect.NavigateToCharacter(CharacterId(Region.EU, "Tarren Mill", "Thrall")),
                awaitItem(),
            )
        }
    }

    @Test
    fun `recent tap navigates and recent remove deletes`() = runTest {
        val id = CharacterId(Region.EU, "tarren-mill", "Gingi")
        recentsRepository.save(RecentSearch(id, searchedAtEpochMillis = 1))
        val vm = viewModel()
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.recentSearches.size)
        vm.effects.test {
            vm.onEvent(HomeEvent.RecentSelected(vm.uiState.value.recentSearches.first()))
            assertEquals(HomeEffect.NavigateToCharacter(id), awaitItem())
        }
        vm.onEvent(HomeEvent.RecentRemoved(id))
        advanceUntilIdle()
        assertEquals(emptyList<RecentSearch>(), vm.uiState.value.recentSearches)
    }
}
