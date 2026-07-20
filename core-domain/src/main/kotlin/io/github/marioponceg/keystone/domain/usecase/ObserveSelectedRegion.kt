package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.repository.RegionPreferenceRepository
import kotlinx.coroutines.flow.Flow

/** Observes the user's selected region preference. */
class ObserveSelectedRegion(private val repository: RegionPreferenceRepository) {
    operator fun invoke(): Flow<Region> = repository.observe()
}
