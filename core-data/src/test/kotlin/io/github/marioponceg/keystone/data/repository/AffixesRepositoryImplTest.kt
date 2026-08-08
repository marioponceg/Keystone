package io.github.marioponceg.keystone.data.repository

import io.github.marioponceg.conduit.conduit
import io.github.marioponceg.conduit.http.HttpResponse
import io.github.marioponceg.conduit.serialization.kotlinx.KotlinxJsonConverter
import io.github.marioponceg.keystone.data.FakeEngine
import io.github.marioponceg.keystone.data.fixture
import io.github.marioponceg.keystone.data.remote.KeystoneJson
import io.github.marioponceg.keystone.data.remote.RaiderIoApi
import io.github.marioponceg.keystone.domain.error.KeystoneError
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.ApiLocale
import io.github.marioponceg.keystone.domain.model.Region
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AffixesRepositoryImplTest {

    private fun repository(engine: FakeEngine): AffixesRepositoryImpl {
        val client = conduit {
            this.engine = engine
            converter = KotlinxJsonConverter(KeystoneJson)
            baseUrl = "https://raider.io"
        }
        return AffixesRepositoryImpl(RaiderIoApi(client))
    }

    @Test
    fun `success maps payload to domain and encodes the query`() = runTest {
        val engine = FakeEngine(HttpResponse(code = 200, body = fixture("affixes.json").encodeToByteArray()))
        val result = repository(engine).getWeeklyAffixes(Region.EU, ApiLocale.ES)
        val success = assertIs<KeystoneResult.Success<*>>(result)
        val affixes = assertIs<io.github.marioponceg.keystone.domain.model.WeeklyAffixes>(success.value)
        assertTrue(affixes.affixes.isNotEmpty())
        val url = checkNotNull(engine.lastRequest).url
        assertTrue(url.contains("region=eu&locale=es"))
    }

    @Test
    fun `http 400 maps to Unknown`() = runTest {
        val engine = FakeEngine(HttpResponse(code = 400, body = """{"error":"bad request"}""".encodeToByteArray()))
        assertEquals(
            KeystoneResult.Failure(KeystoneError.Unknown),
            repository(engine).getWeeklyAffixes(Region.EU, ApiLocale.EN),
        )
    }
}
