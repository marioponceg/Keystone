package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterProfileTest {
    @Test
    fun `CharacterProfile stores id`() {
        val id = CharacterId(Region.EU, "realm", "name")
        val profile = CharacterProfile(
            id = id,
            name = "name",
            realm = "realm",
            characterClass = "Warrior",
            spec = "Arms",
            score = SeasonScore(100.0, "#ff0000", emptyList()),
            bestRuns = emptyList(),
        )
        assertEquals(id, profile.id)
    }

    @Test
    fun `CharacterProfile stores name`() {
        val profile = CharacterProfile(
            id = CharacterId(Region.EU, "realm", "name"),
            name = "Thrall",
            realm = "realm",
            characterClass = "Warrior",
            spec = "Arms",
            score = SeasonScore(100.0, "#ff0000", emptyList()),
            bestRuns = emptyList(),
        )
        assertEquals("Thrall", profile.name)
    }

    @Test
    fun `CharacterProfile stores realm`() {
        val profile = CharacterProfile(
            id = CharacterId(Region.EU, "tarren-mill", "name"),
            name = "name",
            realm = "tarren-mill",
            characterClass = "Warrior",
            spec = "Arms",
            score = SeasonScore(100.0, "#ff0000", emptyList()),
            bestRuns = emptyList(),
        )
        assertEquals("tarren-mill", profile.realm)
    }

    @Test
    fun `CharacterProfile stores characterClass`() {
        val profile = CharacterProfile(
            id = CharacterId(Region.EU, "realm", "name"),
            name = "name",
            realm = "realm",
            characterClass = "Warrior",
            spec = "Arms",
            score = SeasonScore(100.0, "#ff0000", emptyList()),
            bestRuns = emptyList(),
        )
        assertEquals("Warrior", profile.characterClass)
    }

    @Test
    fun `CharacterProfile stores spec`() {
        val profile = CharacterProfile(
            id = CharacterId(Region.EU, "realm", "name"),
            name = "name",
            realm = "realm",
            characterClass = "Warrior",
            spec = "Arms",
            score = SeasonScore(100.0, "#ff0000", emptyList()),
            bestRuns = emptyList(),
        )
        assertEquals("Arms", profile.spec)
    }

    @Test
    fun `CharacterProfile stores score`() {
        val score = SeasonScore(100.0, "#ff0000", emptyList())
        val profile = CharacterProfile(
            id = CharacterId(Region.EU, "realm", "name"),
            name = "name",
            realm = "realm",
            characterClass = "Warrior",
            spec = "Arms",
            score = score,
            bestRuns = emptyList(),
        )
        assertEquals(score, profile.score)
    }

    @Test
    fun `CharacterProfile stores bestRuns`() {
        val runs = emptyList<DungeonRun>()
        val profile = CharacterProfile(
            id = CharacterId(Region.EU, "realm", "name"),
            name = "name",
            realm = "realm",
            characterClass = "Warrior",
            spec = "Arms",
            score = SeasonScore(100.0, "#ff0000", emptyList()),
            bestRuns = runs,
        )
        assertEquals(runs, profile.bestRuns)
    }
}

class SeasonScoreTest {
    @Test
    fun `SeasonScore stores overall`() {
        val score = SeasonScore(100.5, "#ff0000", emptyList())
        assertEquals(100.5, score.overall)
    }

    @Test
    fun `SeasonScore stores colorHex`() {
        val score = SeasonScore(100.0, "#ff0000", emptyList())
        assertEquals("#ff0000", score.colorHex)
    }

    @Test
    fun `SeasonScore stores roles`() {
        val roles = listOf(RoleScore(Role.TANK, 100.0, "#ff0000"))
        val score = SeasonScore(100.0, "#ff0000", roles)
        assertEquals(roles, score.roles)
    }
}

class RoleScoreTest {
    @Test
    fun `RoleScore stores role`() {
        val roleScore = RoleScore(Role.TANK, 100.0, "#ff0000")
        assertEquals(Role.TANK, roleScore.role)
    }

    @Test
    fun `RoleScore stores score`() {
        val roleScore = RoleScore(Role.TANK, 100.5, "#ff0000")
        assertEquals(100.5, roleScore.score)
    }

    @Test
    fun `RoleScore stores colorHex`() {
        val roleScore = RoleScore(Role.TANK, 100.0, "#ff0000")
        assertEquals("#ff0000", roleScore.colorHex)
    }
}

class RoleTest {
    @Test
    fun `Role has TANK variant`() {
        assertEquals(Role.TANK, Role.TANK)
    }

    @Test
    fun `Role has HEALER variant`() {
        assertEquals(Role.HEALER, Role.HEALER)
    }

    @Test
    fun `Role has DPS variant`() {
        assertEquals(Role.DPS, Role.DPS)
    }
}
