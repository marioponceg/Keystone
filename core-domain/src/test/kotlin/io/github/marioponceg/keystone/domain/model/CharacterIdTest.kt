package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CharacterIdTest {
    @Test
    fun `CharacterId stores region`() {
        val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        assertEquals(Region.EU, id.region)
    }

    @Test
    fun `CharacterId stores realm`() {
        val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        assertEquals(Realm("Tarren Mill", "tarren-mill"), id.realm)
    }

    @Test
    fun `CharacterId stores name`() {
        val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        assertEquals("Thrall", id.name)
    }

    @Test
    fun `CharacterId equality based on region, realm, and name`() {
        val id1 = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        val id2 = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        assertEquals(id1, id2)
    }

    @Test
    fun `CharacterId inequality when region differs`() {
        val id1 = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        val id2 = CharacterId(Region.US, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        assertNotEquals(id1, id2)
    }

    @Test
    fun `CharacterId inequality when realm differs`() {
        val id1 = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        val id2 = CharacterId(Region.EU, Realm("Area 52", "area-52"), "Thrall")
        assertNotEquals(id1, id2)
    }

    @Test
    fun `CharacterId inequality when name differs`() {
        val id1 = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Thrall")
        val id2 = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Forsaken")
        assertNotEquals(id1, id2)
    }
}
