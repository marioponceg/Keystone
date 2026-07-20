package io.github.marioponceg.keystone.storage

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
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

class RegionPreferenceDataStoreTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun store(): RegionPreferenceDataStore = RegionPreferenceDataStore(
        PreferenceDataStoreFactory.create(scope = scope) {
            tmp.newFile("region.preferences_pb")
        },
    )

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `observe defaults to EU when nothing saved`() = runBlocking {
        val store = store()
        assertEquals(Region.EU, store.observe().first())
    }

    @Test
    fun `save and observe round-trips selected region`() = runBlocking {
        val store = store()
        store.save(Region.US)
        assertEquals(Region.US, store.observe().first())
    }
}
