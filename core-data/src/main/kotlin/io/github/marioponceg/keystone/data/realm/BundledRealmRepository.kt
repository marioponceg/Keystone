package io.github.marioponceg.keystone.data.realm

import io.github.marioponceg.keystone.data.remote.KeystoneJson
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.repository.RealmRepository
import kotlinx.serialization.json.Json

/** Reads the committed per-region realm snapshots from the classpath. */
class BundledRealmRepository(private val json: Json = KeystoneJson) : RealmRepository {

    override fun realms(region: Region): List<Realm> {
        val resource = javaClass.getResourceAsStream("/realms/${region.slug}.json")
            ?: return emptyList()
        val raw = resource.bufferedReader().use { it.readText() }
        return runCatching { json.decodeFromString<List<RealmDto>>(raw) }
            .getOrDefault(emptyList())
            .map { Realm(name = it.name, slug = it.slug) }
    }
}
