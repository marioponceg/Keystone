package io.github.marioponceg.keystone.data.realm

import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BundledRealmRepositoryTest {

    private val repository = BundledRealmRepository()

    @Test
    fun `eu realms include Tarren Mill with its slug`() {
        val eu = repository.realms(Region.EU)
        assertContains(eu, Realm("Tarren Mill", "tarren-mill"))
    }

    @Test
    fun `every region loads a non-empty list`() {
        Region.entries.forEach { region ->
            assertTrue(repository.realms(region).isNotEmpty(), "empty realms for $region")
        }
    }

    @Test
    fun `realms are sorted alphabetically by name`() {
        val names = repository.realms(Region.EU).map { it.name }
        assertEquals(names.sortedBy { it.lowercase() }, names)
    }
}
