package io.github.marioponceg.keystone.domain.repository

import io.github.marioponceg.keystone.domain.model.Region
import kotlinx.coroutines.flow.Flow

interface RegionPreferenceRepository {
    /** Emits the last selected region, defaulting to [Region.EU]. */
    fun observe(): Flow<Region>

    suspend fun save(region: Region)
}
