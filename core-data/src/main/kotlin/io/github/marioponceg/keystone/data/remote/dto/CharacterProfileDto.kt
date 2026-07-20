package io.github.marioponceg.keystone.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterProfileDto(
    val name: String,
    @SerialName("class") val characterClass: String,
    @SerialName("active_spec_name") val activeSpecName: String,
    val realm: String,
    @SerialName("mythic_plus_scores_by_season") val scoresBySeason: List<SeasonScoresDto> = emptyList(),
    @SerialName("mythic_plus_best_runs") val bestRuns: List<BestRunDto> = emptyList(),
)

@Serializable
data class SeasonScoresDto(
    val season: String,
    val segments: SegmentsDto,
)

@Serializable
data class SegmentsDto(
    val all: SegmentDto,
    val dps: SegmentDto? = null,
    val healer: SegmentDto? = null,
    val tank: SegmentDto? = null,
)

@Serializable
data class SegmentDto(
    val score: Double,
    val color: String,
)

@Serializable
data class BestRunDto(
    val dungeon: String,
    @SerialName("short_name") val shortName: String,
    @SerialName("mythic_level") val mythicLevel: Int,
    @SerialName("clear_time_ms") val clearTimeMs: Long,
    @SerialName("par_time_ms") val parTimeMs: Long,
    @SerialName("num_keystone_upgrades") val numKeystoneUpgrades: Int,
    val score: Double,
)
