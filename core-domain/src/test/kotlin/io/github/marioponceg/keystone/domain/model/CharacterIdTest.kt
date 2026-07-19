package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CharacterIdTest {
    @Test
    fun `CharacterId stores region`() {
        val id = CharacterId(Region.EU, "tarren-mill", "Thrall")
        assertEquals(Region.EU, id.region)
    }

    @Test
    fun `CharacterId stores realm`() {
        val id = CharacterId(Region.EU, "tarren-mill", "Thrall")
        assertEquals("tarren-mill", id.realm)
    }

    @Test
    fun `CharacterId stores name`() {
        val id = CharacterId(Region.EU, "tarren-mill", "Thrall")
        assertEquals("Thrall", id.name)
    }

    @Test
    fun `CharacterId equality based on region, realm, and name`() {
        val id1 = CharacterId(Region.EU, "tarren-mill", "Thrall")
        val id2 = CharacterId(Region.EU, "tarren-mill", "Thrall")
        assertEquals(id1, id2)
    }

    @Test
    fun `CharacterId inequality when region differs`() {
        val id1 = CharacterId(Region.EU, "tarren-mill", "Thrall")
        val id2 = CharacterId(Region.US, "tarren-mill", "Thrall")
        assertNotEquals(id1, id2)
    }

    @Test
    fun `CharacterId inequality when realm differs`() {
        val id1 = CharacterId(Region.EU, "tarren-mill", "Thrall")
        val id2 = CharacterId(Region.EU, "area-52", "Thrall")
        assertNotEquals(id1, id2)
    }

    @Test
    fun `CharacterId inequality when name differs`() {
        val id1 = CharacterId(Region.EU, "tarren-mill", "Thrall")
        val id2 = CharacterId(Region.EU, "tarren-mill", "Forsaken")
        assertNotEquals(id1, id2)
    }
}
