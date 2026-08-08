package io.github.marioponceg.keystone.ui

import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.ApiLocale
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes
import io.github.marioponceg.keystone.domain.model.push
import io.github.marioponceg.keystone.domain.repository.AffixesRepository
import io.github.marioponceg.keystone.domain.repository.AppLocaleProvider
import io.github.marioponceg.keystone.domain.repository.CharacterRepository
import io.github.marioponceg.keystone.domain.repository.RealmRepository
import io.github.marioponceg.keystone.domain.repository.RecentSearchesRepository
import io.github.marioponceg.keystone.domain.repository.RegionPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCharacterRepository(var result: KeystoneResult<CharacterProfile>) : CharacterRepository {
    var lastRequested: CharacterId? = null
    override suspend fun getProfile(id: CharacterId): KeystoneResult<CharacterProfile> {
        lastRequested = id
        return result
    }
}

class FakeAffixesRepository(var result: KeystoneResult<WeeklyAffixes>) : AffixesRepository {
    var lastRegion: Region? = null
    var lastLocale: ApiLocale? = null
    override suspend fun getWeeklyAffixes(region: Region, locale: ApiLocale): KeystoneResult<WeeklyAffixes> {
        lastRegion = region
        lastLocale = locale
        return result
    }
}

class FakeAppLocaleProvider(var locale: ApiLocale = ApiLocale.EN) : AppLocaleProvider {
    override fun current(): ApiLocale = locale
}

class FakeRecentSearchesRepository : RecentSearchesRepository {
    val searches = MutableStateFlow<List<RecentSearch>>(emptyList())
    override fun observe(): Flow<List<RecentSearch>> = searches
    override suspend fun save(search: RecentSearch) {
        searches.value = searches.value.push(search)
    }
    override suspend fun remove(id: CharacterId) {
        searches.value = searches.value.filterNot { it.id == id }
    }
}

class FakeRegionPreferenceRepository : RegionPreferenceRepository {
    val region = MutableStateFlow(Region.EU)
    override fun observe(): Flow<Region> = region
    override suspend fun save(region: Region) {
        this.region.value = region
    }
}

class FakeRealmRepository(private val byRegion: Map<Region, List<Realm>>) : RealmRepository {
    override fun realms(region: Region): List<Realm> = byRegion[region].orEmpty()
}
