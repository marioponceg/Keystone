package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.repository.CharacterRepository

/** Loads a character's current-season M+ profile. */
class GetCharacterProfile(private val repository: CharacterRepository) {
    suspend operator fun invoke(id: CharacterId): KeystoneResult<CharacterProfile> =
        repository.getProfile(id)
}
