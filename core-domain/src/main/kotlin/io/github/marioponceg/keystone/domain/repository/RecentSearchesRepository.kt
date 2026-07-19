package io.github.marioponceg.keystone.domain.repository

import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

interface RecentSearchesRepository {
    /** Emits the recent searches, newest first. */
    fun observe(): Flow<List<RecentSearch>>

    /** Persists [search] applying [io.github.marioponceg.keystone.domain.model.push] semantics. */
    suspend fun save(search: RecentSearch)

    suspend fun remove(id: CharacterId)
}
