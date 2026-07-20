package io.github.marioponceg.keystone.data.mapper

import io.github.marioponceg.keystone.data.remote.dto.AffixesDto
import io.github.marioponceg.keystone.domain.model.Affix
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes

fun AffixesDto.toDomain(): WeeklyAffixes = WeeklyAffixes(
    title = title,
    affixes = affixDetails.map { Affix(name = it.name, description = it.description) },
)
