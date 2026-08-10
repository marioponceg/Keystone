package io.github.marioponceg.keystone.ui.home

import app.cash.turbine.test
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.usecase.GetRealms
import io.github.marioponceg.keystone.domain.usecase.ObserveRecentSearches
import io.github.marioponceg.keystone.domain.usecase.ObserveSelectedRegion
import io.github.marioponceg.keystone.domain.usecase.RemoveRecentSearch
import io.github.marioponceg.keystone.domain.usecase.SaveSelectedRegion
import io.github.marioponceg.keystone.ui.FakeRealmRepository
import io.github.marioponceg.keystone.ui.FakeRecentSearchesRepository
import io.github.marioponceg.keystone.ui.FakeRegionPreferenceRepository
import io.github.marioponceg.keystone.ui.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val recentsRepository = FakeRecentSearchesRepository()
    private val regionRepository = FakeRegionPreferenceRepository()
    private val euRealms = listOf(
        Realm("Aggra", "aggra"),
        Realm("Tarren Mill", "tarren-mill"),
        Realm("Zul'jin", "zuljin"),
    )
    private val realmRepository = FakeRealmRepository(mapOf(Region.EU to euRealms))

    private fun viewModel() = HomeViewModel(
        getRealms = GetRealms(realmRepository),
        observeRecentSearches = ObserveRecentSearches(recentsRepository),
        removeRecentSearch = RemoveRecentSearch(recentsRepository),
        observeSelectedRegion = ObserveSelectedRegion(regionRepository),
        saveSelectedRegion = SaveSelectedRegion(regionRepository),
    )

    @Test
    fun `region change persists`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.RegionSelected(Region.US))
        advanceUntilIdle()
        assertEquals(Region.US, regionRepository.region.value)
    }

    @Test
    fun `tapping the realm field opens the sheet with the region's realms`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.RealmFieldTapped)
        assertEquals(true, vm.uiState.value.isRealmSheetVisible)
        assertEquals(euRealms, vm.uiState.value.realmResults)
    }

    @Test
    fun `realm query filters case-insensitively by name`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.RealmFieldTapped)
        vm.onEvent(HomeEvent.RealmQueryChanged("zul"))
        assertEquals(listOf(Realm("Zul'jin", "zuljin")), vm.uiState.value.realmResults)
    }

    @Test
    fun `selecting a realm sets it, closes the sheet and enables search`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.NameChanged("Thrall"))
        vm.onEvent(HomeEvent.RealmSelected(Realm("Tarren Mill", "tarren-mill")))
        assertEquals(Realm("Tarren Mill", "tarren-mill"), vm.uiState.value.selectedRealm)
        assertEquals(false, vm.uiState.value.isRealmSheetVisible)
        assertEquals(true, vm.uiState.value.canSearch)
    }

    @Test
    fun `changing region clears the selected realm`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.RealmSelected(Realm("Tarren Mill", "tarren-mill")))
        vm.onEvent(HomeEvent.RegionSelected(Region.US))
        assertEquals(null, vm.uiState.value.selectedRealm)
        assertEquals(false, vm.uiState.value.canSearch)
    }

    @Test
    fun `search submit navigates with the selected realm's slug`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onEvent(HomeEvent.NameChanged(" Thrall "))
        vm.onEvent(HomeEvent.RealmSelected(Realm("Tarren Mill", "tarren-mill")))
        vm.effects.test {
            vm.onEvent(HomeEvent.SearchSubmitted)
            assertEquals(
                HomeEffect.NavigateToCharacter(CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")),
                awaitItem(),
            )
        }
    }

    @Test
    fun `recent tap navigates and recent remove deletes`() = runTest {
        val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Gingi")
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
