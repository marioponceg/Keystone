package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes
import io.github.marioponceg.keystone.domain.repository.AffixesRepository
import io.github.marioponceg.keystone.domain.repository.AppLocaleProvider

/**
 * Loads the current week's affixes for a region, in the device's language.
 *
 * The locale is read on every call rather than captured once, so changing the device or per-app
 * language takes effect on the next load without any cache to invalidate.
 */
class GetWeeklyAffixes(
    private val repository: AffixesRepository,
    private val localeProvider: AppLocaleProvider,
) {
    suspend operator fun invoke(region: Region): KeystoneResult<WeeklyAffixes> =
        repository.getWeeklyAffixes(region, localeProvider.current())
}
