package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.error.KeystoneError
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.fake.FakeAffixesRepository
import io.github.marioponceg.keystone.domain.fake.FakeCharacterRepository
import io.github.marioponceg.keystone.domain.fake.FakeRecentSearchesRepository
import io.github.marioponceg.keystone.domain.fake.FakeRegionPreferenceRepository
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UseCasesTest {

    private val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")

    @Test
    fun `GetCharacterProfile delegates to the repository`() = runTest {
        val repository = FakeCharacterRepository(KeystoneResult.Failure(KeystoneError.CharacterNotFound))
        val result = GetCharacterProfile(repository)(id)
        assertEquals(KeystoneResult.Failure(KeystoneError.CharacterNotFound), result)
        assertEquals(id, repository.lastRequested)
    }

    @Test
    fun `GetWeeklyAffixes delegates to the repository`() = runTest {
        val repository = FakeAffixesRepository(KeystoneResult.Failure(KeystoneError.Network))
        val result = GetWeeklyAffixes(repository)(Region.US)
        assertEquals(KeystoneResult.Failure(KeystoneError.Network), result)
        assertEquals(Region.US, repository.lastRegion)
    }

    @Test
    fun `ObserveRecentSearches returns repository flow`() = runTest {
        val repository = FakeRecentSearchesRepository()
        repository.searches.value = listOf(RecentSearch(id, 100))
        val flow = ObserveRecentSearches(repository)()
        val result = flow.first()
        assertEquals(listOf(id), result.map { it.id })
    }

    @Test
    fun `SaveRecentSearch persists through the repository`() = runTest {
        val repository = FakeRecentSearchesRepository()
        SaveRecentSearch(repository)(RecentSearch(id, searchedAtEpochMillis = 42))
        assertEquals(listOf(id), repository.searches.first().map { it.id })
    }

    @Test
    fun `RemoveRecentSearch removes through the repository`() = runTest {
        val repository = FakeRecentSearchesRepository()
        repository.searches.value = listOf(RecentSearch(id, 100))
        RemoveRecentSearch(repository)(id)
        assertEquals(emptyList(), repository.searches.first())
    }

    @Test
    fun `ObserveSelectedRegion returns repository flow`() = runTest {
        val repository = FakeRegionPreferenceRepository()
        repository.region.value = Region.US
        val flow = ObserveSelectedRegion(repository)()
        val result = flow.first()
        assertEquals(Region.US, result)
    }

    @Test
    fun `SaveSelectedRegion persists through the repository`() = runTest {
        val repository = FakeRegionPreferenceRepository()
        SaveSelectedRegion(repository)(Region.TW)
        assertEquals(Region.TW, repository.region.first())
    }
}
