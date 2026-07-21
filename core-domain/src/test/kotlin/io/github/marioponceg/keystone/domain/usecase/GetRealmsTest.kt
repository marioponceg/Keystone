package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.repository.RealmRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRealmsTest {

    @Test
    fun `delegates to the repository for the requested region`() {
        val expected = listOf(Realm("Aggra", "aggra"), Realm("Zul'jin", "zuljin"))
        var requested: Region? = null
        val repository = object : RealmRepository {
            override fun realms(region: Region): List<Realm> {
                requested = region
                return expected
            }
        }
        val result = GetRealms(repository)(Region.US)
        assertEquals(expected, result)
        assertEquals(Region.US, requested)
    }
}
