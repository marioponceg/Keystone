package io.github.marioponceg.keystone.domain.fake

import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes
import io.github.marioponceg.keystone.domain.model.push
import io.github.marioponceg.keystone.domain.repository.AffixesRepository
import io.github.marioponceg.keystone.domain.repository.CharacterRepository
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
    override suspend fun getWeeklyAffixes(region: Region): KeystoneResult<WeeklyAffixes> {
        lastRegion = region
        return result
    }
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
