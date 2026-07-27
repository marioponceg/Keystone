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
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CharacterRepositoryImplTest {

    private val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Zoyu")

    private fun repository(engine: FakeEngine): CharacterRepositoryImpl {
        val client = conduit {
            this.engine = engine
            converter = KotlinxJsonConverter(KeystoneJson)
            baseUrl = "https://raider.io"
        }
        return CharacterRepositoryImpl(RaiderIoApi(client))
    }

    @Test
    fun `success maps payload to domain and encodes the query`() = runTest {
        val engine = FakeEngine(HttpResponse(code = 200, body = fixture("character_profile.json").encodeToByteArray()))
        val result = repository(engine).getProfile(id)
        val profile = assertIs<KeystoneResult.Success<*>>(result)
        val url = checkNotNull(engine.lastRequest).url
        assertTrue(url.contains("region=eu"))
        assertTrue(url.contains("realm=tarren-mill"))
        assertTrue(url.contains("name=Zoyu"))
        val encodedFields = url.contains("fields=mythic_plus_scores_by_season%3Acurrent%2Cmythic_plus_best_runs")
        val plainFields = url.contains("fields=mythic_plus_scores_by_season:current,mythic_plus_best_runs")
        assertTrue(encodedFields || plainFields)
        assertTrue(profile.value != null)
    }

    @Test
    fun `http 400 maps to CharacterNotFound`() = runTest {
        val engine = FakeEngine(
            HttpResponse(code = 400, body = """{"error":"Could not find requested character"}""".encodeToByteArray()),
        )
        assertEquals(
            KeystoneResult.Failure(KeystoneError.CharacterNotFound),
            repository(engine).getProfile(id),
        )
    }

    @Test
    fun `io failure maps to Network`() = runTest {
        val engine = FakeEngine(error = IOException("boom"))
        assertEquals(
            KeystoneResult.Failure(KeystoneError.Network),
            repository(engine).getProfile(id),
        )
    }

    @Test
    fun `http 500 and malformed body map to Unknown`() = runTest {
        assertEquals(
            KeystoneResult.Failure(KeystoneError.Unknown),
            repository(FakeEngine(HttpResponse(code = 500))).getProfile(id),
        )
        assertEquals(
            KeystoneResult.Failure(KeystoneError.Unknown),
            repository(FakeEngine(HttpResponse(code = 200, body = "not json".encodeToByteArray()))).getProfile(id),
        )
    }
}
