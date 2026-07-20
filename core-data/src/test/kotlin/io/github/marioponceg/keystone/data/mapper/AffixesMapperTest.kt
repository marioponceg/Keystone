package io.github.marioponceg.keystone.data.mapper

import io.github.marioponceg.keystone.data.fixture
import io.github.marioponceg.keystone.data.remote.KeystoneJson
import io.github.marioponceg.keystone.data.remote.dto.AffixDetailDto
import io.github.marioponceg.keystone.data.remote.dto.AffixesDto
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AffixesMapperTest {

    @Test
    fun `decodes and maps the real affixes payload`() {
        val dto = KeystoneJson.decodeFromString<AffixesDto>(fixture("affixes.json"))
        val weekly = dto.toDomain()
        assertEquals(
            "Xal'atath's Bargain: Pulsar, Fortified, Tyrannical, Xal'atath's Guile",
            weekly.title,
        )
        assertEquals(4, weekly.affixes.size)
        assertTrue(weekly.affixes.all { it.name.isNotBlank() && it.description.isNotBlank() })
        assertEquals("Xal'atath's Bargain: Pulsar", weekly.affixes.first().name)
        assertEquals(
            "While in combat, Xal'atath summons pulsars that orbit players.",
            weekly.affixes.first().description,
        )
    }

    @Test
    fun `dto round-trips through the shared Json config`() {
        val dto = KeystoneJson.decodeFromString<AffixesDto>(fixture("affixes.json"))
        val reencoded = KeystoneJson.decodeFromString<AffixesDto>(KeystoneJson.encodeToString(dto))
        assertEquals(dto, reencoded)
    }

    @Test
    fun `decoded affix details equal a literal reconstruction of the same fixture values`() {
        val detail = KeystoneJson.decodeFromString<AffixesDto>(fixture("affixes.json"))
            .affixDetails
            .first()
        val expected = AffixDetailDto(
            name = "Xal'atath's Bargain: Pulsar",
            description = "While in combat, Xal'atath summons pulsars that orbit players.",
        )
        assertEquals(expected, detail)
        // A different name must not compare equal, proving equals() actually checks values.
        assertTrue(expected != expected.copy(name = "Something else"))
    }
}
