package io.github.marioponceg.keystone.storage

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

class RecentSearchesDataStoreTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun store(): RecentSearchesDataStore = RecentSearchesDataStore(
        PreferenceDataStoreFactory.create(scope = scope) {
            tmp.newFile("recents.preferences_pb")
        },
    )

    @After
    fun tearDown() {
        scope.cancel()
    }

    private fun search(name: String, at: Long = 1) =
        RecentSearch(
            CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), name),
            searchedAtEpochMillis = at,
        )

    @Test
    fun `save and observe round-trips newest first`() = runBlocking {
        val store = store()
        store.save(search("a", 1))
        store.save(search("b", 2))
        assertEquals(listOf("b", "a"), store.observe().first().map { it.id.name })
    }

    @Test
    fun `save applies push semantics through persistence`() = runBlocking {
        val store = store()
        repeat(11) { store.save(search("c$it", it.toLong())) }
        assertEquals(10, store.observe().first().size)
    }

    @Test
    fun `remove deletes by id`() = runBlocking {
        val store = store()
        store.save(search("a"))
        store.save(search("b"))
        store.remove(search("a").id)
        assertEquals(listOf("b"), store.observe().first().map { it.id.name })
    }

    @Test
    fun `entries stored under the old string-realm schema are discarded`() = runBlocking {
        val store = store()
        // v0.1 shape: realm as a bare string, no realm_slug/realm_name.
        store.rawWrite("""[{"region":"EU","realm":"tarren-mill","name":"Old","searched_at":1}]""")
        assertEquals(emptyList<String>(), store.observe().first().map { it.id.name })
    }
}
