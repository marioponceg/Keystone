package io.github.marioponceg.keystone.ui.week

import app.cash.turbine.test
import io.github.marioponceg.keystone.domain.error.KeystoneError
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.Affix
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes
import io.github.marioponceg.keystone.domain.usecase.GetWeeklyAffixes
import io.github.marioponceg.keystone.domain.usecase.ObserveSelectedRegion
import io.github.marioponceg.keystone.ui.FakeAffixesRepository
import io.github.marioponceg.keystone.ui.FakeAppLocaleProvider
import io.github.marioponceg.keystone.ui.FakeRegionPreferenceRepository
import io.github.marioponceg.keystone.ui.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class WeekViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val affixes = WeeklyAffixes(
        title = "Tyrannical, Bolstering and Raging",
        affixes = listOf(Affix("Tyrannical", "Boss enemies have 30% more health.")),
    )

    private fun viewModel(
        result: KeystoneResult<WeeklyAffixes>,
        regionRepository: FakeRegionPreferenceRepository = FakeRegionPreferenceRepository(),
    ): Pair<WeekViewModel, FakeAffixesRepository> {
        val repository = FakeAffixesRepository(result)
        val vm = WeekViewModel(
            getWeeklyAffixes = GetWeeklyAffixes(repository, FakeAppLocaleProvider()),
            observeSelectedRegion = ObserveSelectedRegion(regionRepository),
        )
        return vm to repository
    }

    @Test
    fun `emits Content when the affixes load`() = runTest {
        val (vm, _) = viewModel(KeystoneResult.Success(affixes))
        advanceUntilIdle()
        vm.uiState.test {
            assertEquals(WeekUiState.Content(affixes), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Unavailable when the call fails`() = runTest {
        val (vm, _) = viewModel(KeystoneResult.Failure(KeystoneError.Network))
        advanceUntilIdle()
        vm.uiState.test {
            assertEquals(WeekUiState.Unavailable, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Retry reloads for the persisted region`() = runTest {
        val regions = FakeRegionPreferenceRepository().apply { region.value = Region.US }
        val (vm, repository) = viewModel(KeystoneResult.Failure(KeystoneError.Network), regions)
        advanceUntilIdle()
        vm.uiState.test {
            assertEquals(WeekUiState.Unavailable, awaitItem())
            repository.result = KeystoneResult.Success(affixes)
            vm.onEvent(WeekEvent.Retry)
            assertEquals(WeekUiState.Loading, awaitItem())
            assertEquals(WeekUiState.Content(affixes), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(Region.US, repository.lastRegion)
    }
}
