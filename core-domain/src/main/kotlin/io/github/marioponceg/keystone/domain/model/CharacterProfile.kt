package io.github.marioponceg.keystone.domain.model

/** A character's Mythic+ profile for the current season. */
data class CharacterProfile(
    val id: CharacterId,
    val name: String,
    val realm: String,
    val characterClass: String,
    val spec: String,
    val score: SeasonScore,
    val bestRuns: List<DungeonRun>,
    /**
     * Raider.IO's `thumbnail_url`, pointing at Blizzard's public render CDN. Null when the
     * character has no rendered portrait — rare, but a missing avatar must not break the screen.
     */
    val avatarUrl: String? = null,
)

/** Season score with the display color Raider.IO assigns, plus the per-role breakdown. */
data class SeasonScore(
    val overall: Double,
    val colorHex: String,
    val roles: List<RoleScore>,
)

data class RoleScore(
    val role: Role,
    val score: Double,
    val colorHex: String,
)

enum class Role { TANK, HEALER, DPS }
