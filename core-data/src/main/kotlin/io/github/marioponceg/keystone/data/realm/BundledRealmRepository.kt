package io.github.marioponceg.keystone.data.realm

import io.github.marioponceg.keystone.data.remote.KeystoneJson
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.repository.RealmRepository
import kotlinx.serialization.json.Json
import java.io.InputStream

/**
 * Reads the committed per-region realm snapshots from the classpath.
 *
 * [openResource] is a seam so the failure paths are testable; production always uses the
 * classpath default.
 */
class BundledRealmRepository(
    private val json: Json = KeystoneJson,
    private val openResource: (String) -> InputStream? = { path ->
        BundledRealmRepository::class.java.getResourceAsStream(path)
    },
) : RealmRepository {

    override fun realms(region: Region): List<Realm> {
        val path = "/realms/${region.slug}.json"
        // A missing snapshot is a packaging bug, not a runtime state — failing loudly surfaces it
        // in CI instead of shipping a picker no realm can ever be selected from. A corrupt payload,
        // by contrast, degrades to an empty list.
        val resource = checkNotNull(openResource(path)) { "Missing bundled realm resource: $path" }
        val raw = resource.bufferedReader().use { it.readText() }
        return runCatching { json.decodeFromString<List<RealmDto>>(raw) }
            .getOrDefault(emptyList())
            .map { Realm(name = it.name, slug = it.slug) }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }
}
