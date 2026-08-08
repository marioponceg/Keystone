package io.github.marioponceg.keystone.domain.repository

import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.ApiLocale
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes

interface AffixesRepository {
    suspend fun getWeeklyAffixes(region: Region, locale: ApiLocale): KeystoneResult<WeeklyAffixes>
}
