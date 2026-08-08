package io.github.marioponceg.keystone.data.remote

import io.github.marioponceg.conduit.ConduitClient
import io.github.marioponceg.conduit.get
import io.github.marioponceg.conduit.result.ConduitResult
import io.github.marioponceg.keystone.data.remote.dto.AffixesDto
import io.github.marioponceg.keystone.data.remote.dto.CharacterProfileDto
import io.github.marioponceg.keystone.domain.model.ApiLocale
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Region
import java.net.URLEncoder

private const val PROFILE_FIELDS = "mythic_plus_scores_by_season:current,mythic_plus_best_runs"

/** Typed facade over the two Raider.IO endpoints v0.1 uses. */
class RaiderIoApi(private val client: ConduitClient) {

    suspend fun getCharacterProfile(id: CharacterId): ConduitResult<CharacterProfileDto> =
        client.get(
            "/api/v1/characters/profile" +
                "?region=${id.region.slug}" +
                "&realm=${id.realm.slug.encodeQuery()}" +
                "&name=${id.name.encodeQuery()}" +
                "&fields=${PROFILE_FIELDS.encodeQuery()}",
        )

    suspend fun getWeeklyAffixes(region: Region, locale: ApiLocale): ConduitResult<AffixesDto> =
        client.get("/api/v1/mythic-plus/affixes?region=${region.slug}&locale=${locale.slug}")
}

private fun String.encodeQuery(): String =
    URLEncoder.encode(this, Charsets.UTF_8).replace("+", "%20")
