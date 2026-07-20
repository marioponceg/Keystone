package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes
import io.github.marioponceg.keystone.domain.repository.AffixesRepository

/** Loads the current week's affixes for a given region. */
class GetWeeklyAffixes(private val repository: AffixesRepository) {
    suspend operator fun invoke(region: Region): KeystoneResult<WeeklyAffixes> =
        repository.getWeeklyAffixes(region)
}
