package io.github.marioponceg.keystone.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.push
import io.github.marioponceg.keystone.domain.repository.RecentSearchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val KEY = stringPreferencesKey("recent_searches")
private val StorageJson = Json { ignoreUnknownKeys = true }

/** Persistence-only shape of a recent search; the domain model stays serialization-free. */
@Serializable
private data class StoredSearch(
    val region: String,
    @SerialName("realm_name") val realmName: String,
    @SerialName("realm_slug") val realmSlug: String,
    val name: String,
    @SerialName("searched_at") val searchedAtEpochMillis: Long,
)

class RecentSearchesDataStore(private val dataStore: DataStore<Preferences>) : RecentSearchesRepository {

    override fun observe(): Flow<List<RecentSearch>> =
        dataStore.data.map { preferences -> decode(preferences[KEY]) }

    override suspend fun save(search: RecentSearch) {
        dataStore.edit { preferences ->
            preferences[KEY] = encode(decode(preferences[KEY]).push(search))
        }
    }

    internal suspend fun rawWrite(raw: String) {
        dataStore.edit { it[KEY] = raw }
    }

    override suspend fun remove(id: CharacterId) {
        dataStore.edit { preferences ->
            preferences[KEY] = encode(decode(preferences[KEY]).filterNot { it.id == id })
        }
    }

    private fun decode(raw: String?): List<RecentSearch> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching { StorageJson.decodeFromString<List<StoredSearch>>(raw) }
            .getOrDefault(emptyList())
            .mapNotNull { stored ->
                val region = Region.entries.firstOrNull { it.name == stored.region } ?: return@mapNotNull null
                RecentSearch(
                    id = CharacterId(region, Realm(stored.realmName, stored.realmSlug), stored.name),
                    searchedAtEpochMillis = stored.searchedAtEpochMillis,
                )
            }
    }

    private fun encode(searches: List<RecentSearch>): String =
        StorageJson.encodeToString(
            searches.map {
                StoredSearch(
                    region = it.id.region.name,
                    realmName = it.id.realm.name,
                    realmSlug = it.id.realm.slug,
                    name = it.id.name,
                    searchedAtEpochMillis = it.searchedAtEpochMillis,
                )
            },
        )
}
