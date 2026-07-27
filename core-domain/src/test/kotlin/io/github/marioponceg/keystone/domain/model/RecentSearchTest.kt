package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class RecentSearchTest {

    private fun search(name: String, at: Long) =
        RecentSearch(CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), name), searchedAtEpochMillis = at)

    @Test
    fun `push places the newest search first`() {
        val list = listOf(search("a", 1)).push(search("b", 2))
        assertEquals(listOf("b", "a"), list.map { it.id.name })
    }

    @Test
    fun `push moves an existing id to the front instead of duplicating`() {
        val list = listOf(search("a", 1), search("b", 2)).push(search("b", 3))
        assertEquals(listOf("b", "a"), list.map { it.id.name })
        assertEquals(3, list.first().searchedAtEpochMillis)
    }

    @Test
    fun `push drops the oldest entry beyond the maximum`() {
        val full = (1..10).map { search("c$it", it.toLong()) }.reversed()
        val list = full.push(search("new", 99))
        assertEquals(10, list.size)
        assertEquals("new", list.first().id.name)
        assertEquals("c2", list.last().id.name)
    }
}
