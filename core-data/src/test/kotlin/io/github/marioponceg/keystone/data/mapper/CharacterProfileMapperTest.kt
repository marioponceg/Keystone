package io.github.marioponceg.keystone.data.mapper

import io.github.marioponceg.keystone.data.fixture
import io.github.marioponceg.keystone.data.remote.KeystoneJson
import io.github.marioponceg.keystone.data.remote.dto.BestRunDto
import io.github.marioponceg.keystone.data.remote.dto.CharacterProfileDto
import io.github.marioponceg.keystone.data.remote.dto.RunAffixDto
import io.github.marioponceg.keystone.data.remote.dto.SeasonScoresDto
import io.github.marioponceg.keystone.data.remote.dto.SegmentDto
import io.github.marioponceg.keystone.data.remote.dto.SegmentsDto
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.Role
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class CharacterProfileMapperTest {

    private val requested = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Zoyu")

    private fun decode(): CharacterProfileDto =
        KeystoneJson.decodeFromString(fixture("character_profile.json"))

    @Test
    fun `decodes the real profile payload`() {
        val dto = decode()
        assertEquals("Zoyu", dto.name)
        assertTrue(dto.bestRuns.isNotEmpty())
    }

    @Test
    fun `maps identity, class and spec`() {
        val profile = decode().toDomain(requested)
        assertEquals(requested, profile.id)
        assertEquals("Zoyu", profile.name)
        assertEquals("Demon Hunter", profile.characterClass)
        assertEquals("Devourer", profile.spec)
        assertEquals("Tarren Mill", profile.realm)
    }

    @Test
    fun `maps overall score with color and only nonzero roles`() {
        val profile = decode().toDomain(requested)
        assertEquals(3006.8, profile.score.overall)
        assertEquals("#3a7fce", profile.score.colorHex)
        assertTrue(profile.score.roles.all { it.score > 0 })
        assertEquals(2, profile.score.roles.size)
        assertEquals(
            setOf(Role.DPS, Role.TANK),
            profile.score.roles.map { it.role }.toSet(),
        )
        val dps = profile.score.roles.first { it.role == Role.DPS }
        assertEquals(2955.7, dps.score)
        assertEquals("#4888c3", dps.colorHex)
        val tank = profile.score.roles.first { it.role == Role.TANK }
        assertEquals(2707.3, tank.score)
        assertEquals("#5fc086", tank.colorHex)
    }

    @Test
    fun `maps best runs with times as durations`() {
        val run = decode().toDomain(requested).bestRuns.first()
        assertEquals("Magisters' Terrace", run.dungeonName)
        assertEquals("MT", run.shortName)
        assertEquals(14, run.keystoneLevel)
        assertEquals(1, run.upgrades)
        assertEquals(400.1, run.score)
        assertEquals(1762744.milliseconds, run.clearTime)
        assertEquals(2040999.milliseconds, run.parTime)
    }

    @Test
    fun `maps the run id, icon and completion timestamp`() {
        val run = decode().toDomain(requested).bestRuns.first()
        assertEquals(14598027L, run.id)
        assertEquals(
            "https://cdn.raiderio.net/images/wow/icons/large/inv_achievement_dungeon_magistersterrace.jpg",
            run.iconUrl,
        )
        // 2026-04-18T20:19:16.000Z
        assertEquals(1776543556000L, run.completedAtEpochMillis)
    }

    @Test
    fun `maps the run affixes in payload order with their icons`() {
        val affixes = decode().toDomain(requested).bestRuns.first().affixes
        assertEquals(
            listOf("Tyrannical", "Fortified", "Xal'atath's Guile"),
            affixes.map { it.name },
        )
        assertEquals(
            "https://cdn.raiderio.net/images/wow/icons/large/achievement_boss_archaedas.jpg",
            affixes.first().iconUrl,
        )
    }

    @Test
    fun `a run without optional fields maps to nulls and an empty affix list`() {
        val dto = KeystoneJson.decodeFromString<CharacterProfileDto>(
            """
            {"name":"X","class":"Mage","active_spec_name":"Frost","realm":"Tarren Mill",
             "mythic_plus_best_runs":[{"dungeon":"Neltharus","short_name":"NELT",
             "mythic_level":10,"clear_time_ms":1000,"par_time_ms":2000,
             "num_keystone_upgrades":1,"score":100.0,"keystone_run_id":42}]}
            """.trimIndent(),
        )
        val run = dto.toDomain(requested).bestRuns.single()
        assertEquals(42L, run.id)
        assertNull(run.iconUrl)
        assertNull(run.completedAtEpochMillis)
        assertTrue(run.affixes.isEmpty())
    }

    @Test
    fun `a malformed completion timestamp maps to null instead of throwing`() {
        val dto = KeystoneJson.decodeFromString<CharacterProfileDto>(
            """
            {"name":"X","class":"Mage","active_spec_name":"Frost","realm":"Tarren Mill",
             "mythic_plus_best_runs":[{"dungeon":"Neltharus","short_name":"NELT",
             "mythic_level":10,"clear_time_ms":1000,"par_time_ms":2000,
             "num_keystone_upgrades":1,"score":100.0,"keystone_run_id":42,
             "completed_at":"last tuesday"}]}
            """.trimIndent(),
        )
        assertNull(dto.toDomain(requested).bestRuns.single().completedAtEpochMillis)
    }

    @Test
    fun `dto round-trips through the shared Json config`() {
        val dto = decode()
        val reencoded = KeystoneJson.decodeFromString<CharacterProfileDto>(
            KeystoneJson.encodeToString(dto),
        )
        assertEquals(dto, reencoded)
    }

    @Test
    fun `decoded nested dtos equal a literal reconstruction of the same fixture values`() {
        val dto = decode()
        val expectedRun = BestRunDto(
            dungeon = "Magisters' Terrace",
            shortName = "MT",
            mythicLevel = 14,
            clearTimeMs = 1762744,
            parTimeMs = 2040999,
            numKeystoneUpgrades = 1,
            score = 400.1,
            keystoneRunId = 14598027,
            iconUrl = "https://cdn.raiderio.net/images/wow/icons/large/" +
                "inv_achievement_dungeon_magistersterrace.jpg",
            completedAt = "2026-04-18T20:19:16.000Z",
            affixes = listOf(
                RunAffixDto(
                    name = "Tyrannical",
                    iconUrl = "https://cdn.raiderio.net/images/wow/icons/large/" +
                        "achievement_boss_archaedas.jpg",
                ),
                RunAffixDto(
                    name = "Fortified",
                    iconUrl = "https://cdn.raiderio.net/images/wow/icons/large/" +
                        "ability_toughness.jpg",
                ),
                RunAffixDto(
                    name = "Xal'atath's Guile",
                    iconUrl = "https://cdn.raiderio.net/images/wow/icons/large/" +
                        "ability_racial_chillofnight.jpg",
                ),
            ),
        )
        assertEquals(expectedRun, dto.bestRuns.first())

        val expectedSeason = SeasonScoresDto(
            season = "season-mn-1",
            segments = SegmentsDto(
                all = SegmentDto(score = 3006.8, color = "#3a7fce"),
                dps = SegmentDto(score = 2955.7, color = "#4888c3"),
                healer = SegmentDto(score = 0.0, color = "#ffffff"),
                tank = SegmentDto(score = 2707.3, color = "#5fc086"),
            ),
        )
        assertEquals(expectedSeason, dto.scoresBySeason.first())

        // A different score must not compare equal, proving equals() actually checks values.
        assertTrue(expectedRun != expectedRun.copy(score = expectedRun.score + 1))
    }

    @Test
    fun `missing season list maps to a zero score`() {
        val dto = KeystoneJson.decodeFromString<CharacterProfileDto>(
            """{"name":"X","class":"Mage","active_spec_name":"Frost","realm":"Tarren Mill"}""",
        )
        val profile = dto.toDomain(requested)
        assertEquals(0.0, profile.score.overall)
        assertTrue(profile.score.roles.isEmpty())
        assertTrue(profile.bestRuns.isEmpty())
    }

    @Test
    fun `carries the avatar url through to the domain`() {
        assertEquals(
            "https://render.worldofwarcraft.com/eu/character/tarren-mill/119/181857655-avatar.jpg" +
                "?alt=/wow/static/images/2d/avatar/10-0.jpg",
            decode().toDomain(requested).avatarUrl,
        )
    }

    @Test
    fun `a profile without an avatar maps to null rather than failing`() {
        // The field is absent from this payload entirely, which is the case that would throw if
        // the DTO property had no default.
        val dto = KeystoneJson.decodeFromString<CharacterProfileDto>(
            """{"name":"X","class":"Mage","active_spec_name":"Frost","realm":"Tarren Mill"}""",
        )
        assertNull(dto.toDomain(requested).avatarUrl)
    }
}
