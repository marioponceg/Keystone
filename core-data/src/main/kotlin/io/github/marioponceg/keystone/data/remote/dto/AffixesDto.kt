package io.github.marioponceg.keystone.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AffixesDto(
    val title: String,
    @SerialName("affix_details") val affixDetails: List<AffixDetailDto> = emptyList(),
)

@Serializable
data class AffixDetailDto(
    val name: String,
    val description: String,
)
