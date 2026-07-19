package io.github.marioponceg.keystone.domain.model

import kotlin.time.Duration

/** The best run for one dungeon. [upgrades] is 0 when the key was depleted, 1–3 otherwise. */
data class DungeonRun(
    val dungeonName: String,
    val shortName: String,
    val keystoneLevel: Int,
    val upgrades: Int,
    val clearTime: Duration,
    val parTime: Duration,
    val score: Double,
) {
    val isTimed: Boolean get() = upgrades > 0
}
