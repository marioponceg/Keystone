package io.github.marioponceg.keystone.navigation

import androidx.navigation3.runtime.NavKey
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

@Serializable
data class CharacterDetailKey(
    val region: String,
    val realmSlug: String,
    val realmName: String,
    val name: String,
) : NavKey

fun CharacterId.toKey(): CharacterDetailKey =
    CharacterDetailKey(region = region.name, realmSlug = realm.slug, realmName = realm.name, name = name)

fun CharacterDetailKey.toCharacterId(): CharacterId =
    CharacterId(region = Region.valueOf(region), realm = Realm(name = realmName, slug = realmSlug), name = name)
