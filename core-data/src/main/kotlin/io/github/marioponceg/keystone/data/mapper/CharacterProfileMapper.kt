package io.github.marioponceg.keystone.data.mapper

import io.github.marioponceg.keystone.data.remote.dto.CharacterProfileDto
import io.github.marioponceg.keystone.data.remote.dto.SegmentDto
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.model.DungeonRun
import io.github.marioponceg.keystone.domain.model.Role
import io.github.marioponceg.keystone.domain.model.RoleScore
import io.github.marioponceg.keystone.domain.model.SeasonScore
import kotlin.time.Duration.Companion.milliseconds

private const val ZERO_SCORE_COLOR = "#ffffff"

fun CharacterProfileDto.toDomain(requested: CharacterId): CharacterProfile {
    val segments = scoresBySeason.firstOrNull()?.segments
    val roles = listOfNotNull(
        segments?.tank?.toRoleScore(Role.TANK),
        segments?.healer?.toRoleScore(Role.HEALER),
        segments?.dps?.toRoleScore(Role.DPS),
    )
    return CharacterProfile(
        id = requested,
        name = name,
        realm = realm,
        characterClass = characterClass,
        spec = activeSpecName,
        score = SeasonScore(
            overall = segments?.all?.score ?: 0.0,
            colorHex = segments?.all?.color ?: ZERO_SCORE_COLOR,
            roles = roles,
        ),
        bestRuns = bestRuns.map { run ->
            DungeonRun(
                dungeonName = run.dungeon,
                shortName = run.shortName,
                keystoneLevel = run.mythicLevel,
                upgrades = run.numKeystoneUpgrades,
                clearTime = run.clearTimeMs.milliseconds,
                parTime = run.parTimeMs.milliseconds,
                score = run.score,
            )
        },
    )
}

private fun SegmentDto.toRoleScore(role: Role): RoleScore? =
    if (score > 0.0) RoleScore(role = role, score = score, colorHex = color) else null
