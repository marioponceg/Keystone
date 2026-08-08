package io.github.marioponceg.keystone.domain.model

import kotlin.time.Duration

/**
 * The best run for one dungeon. [upgrades] is 0 when the key was depleted, 1–3 otherwise.
 *
 * [completedAtEpochMillis] is a primitive rather than an `Instant`: `java.time` would break this
 * module's KMP-ready promise, `kotlinx-datetime` would be its first third-party runtime
 * dependency, and `kotlin.time.Instant` is still `@ExperimentalTime` in Kotlin 2.2.10, which would
 * push an opt-in onto every consumer. The ISO-8601 parse lives in `core-data`.
 */
data class DungeonRun(
    val id: Long,
    val dungeonName: String,
    val shortName: String,
    val keystoneLevel: Int,
    val upgrades: Int,
    val clearTime: Duration,
    val parTime: Duration,
    val score: Double,
    val iconUrl: String?,
    val completedAtEpochMillis: Long?,
    val affixes: List<RunAffix>,
    val url: String?,
) {
    val isTimed: Boolean get() = upgrades > 0
}
