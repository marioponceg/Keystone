package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.repository.RegionPreferenceRepository

/** Saves the user's selected region preference. */
class SaveSelectedRegion(private val repository: RegionPreferenceRepository) {
    suspend operator fun invoke(region: Region) {
        repository.save(region)
    }
}
