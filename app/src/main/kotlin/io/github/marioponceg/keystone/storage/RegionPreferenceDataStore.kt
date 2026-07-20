package io.github.marioponceg.keystone.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.repository.RegionPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val KEY = stringPreferencesKey("selected_region")

class RegionPreferenceDataStore(private val dataStore: DataStore<Preferences>) : RegionPreferenceRepository {

    override fun observe(): Flow<Region> =
        dataStore.data.map { preferences ->
            Region.entries.firstOrNull { it.name == preferences[KEY] } ?: Region.EU
        }

    override suspend fun save(region: Region) {
        dataStore.edit { it[KEY] = region.name }
    }
}
