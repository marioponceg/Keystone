package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterProfileTest {
    @Test
    fun `CharacterProfile exposes all fields`() {
        val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        val roles = listOf(RoleScore(Role.DPS, 2801.4, "#ff8000"))
        val score = SeasonScore(2815.2, "#ff8000", roles)
        val runs = emptyList<DungeonRun>()
        val profile = CharacterProfile(
            id = id,
            name = "Thrall",
            realm = "Tarren Mill",
            characterClass = "Shaman",
            spec = "Enhancement",
            score = score,
            bestRuns = runs,
        )
        assertEquals(id, profile.id)
        assertEquals("Thrall", profile.name)
        assertEquals("Tarren Mill", profile.realm)
        assertEquals("Shaman", profile.characterClass)
        assertEquals("Enhancement", profile.spec)
        assertEquals(score, profile.score)
        assertEquals(runs, profile.bestRuns)
    }
}

class SeasonScoreTest {
    @Test
    fun `SeasonScore exposes all fields`() {
        val roles = listOf(RoleScore(Role.TANK, 2500.0, "#a335ee"))
        val score = SeasonScore(2600.5, "#ff8000", roles)
        assertEquals(2600.5, score.overall)
        assertEquals("#ff8000", score.colorHex)
        assertEquals(roles, score.roles)
    }
}

class RoleScoreTest {
    @Test
    fun `RoleScore exposes all fields`() {
        val roleScore = RoleScore(Role.HEALER, 2450.7, "#a335ee")
        assertEquals(Role.HEALER, roleScore.role)
        assertEquals(2450.7, roleScore.score)
        assertEquals("#a335ee", roleScore.colorHex)
    }
}

class RoleTest {
    @Test
    fun `Role declares exactly tank, healer and dps`() {
        assertEquals(listOf(Role.TANK, Role.HEALER, Role.DPS), Role.entries.toList())
    }
}
