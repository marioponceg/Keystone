package io.github.marioponceg.keystone.data.repository

import io.github.marioponceg.keystone.data.mapper.toDomain
import io.github.marioponceg.keystone.data.remote.RaiderIoApi
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.ApiLocale
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes
import io.github.marioponceg.keystone.domain.repository.AffixesRepository

class AffixesRepositoryImpl(private val api: RaiderIoApi) : AffixesRepository {

    override suspend fun getWeeklyAffixes(region: Region, locale: ApiLocale): KeystoneResult<WeeklyAffixes> =
        api.getWeeklyAffixes(region, locale).toKeystoneResult(notFoundOnBadRequest = false) { it.toDomain() }
}
