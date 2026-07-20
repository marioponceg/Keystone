package io.github.marioponceg.keystone.domain.repository

import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile

interface CharacterRepository {
    suspend fun getProfile(id: CharacterId): KeystoneResult<CharacterProfile>
}
