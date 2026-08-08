package io.github.marioponceg.keystone.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterProfileDto(
    val name: String,
    @SerialName("class") val characterClass: String,
    @SerialName("active_spec_name") val activeSpecName: String,
    val realm: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
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
    // Run identity, so it is required exactly like `dungeon` and `mythic_level`. A default would
    // have to fabricate a key, and a fabricated key is worse than a loud failure.
    @SerialName("keystone_run_id") val keystoneRunId: Long,
    // Optional with defaults, the same treatment `thumbnail_url` gets: a missing one degrades to
    // "not shown" instead of failing the whole profile.
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("completed_at") val completedAt: String? = null,
    val affixes: List<RunAffixDto> = emptyList(),
    val url: String? = null,
)

@Serializable
data class RunAffixDto(
    val name: String,
    @SerialName("icon_url") val iconUrl: String? = null,
)
