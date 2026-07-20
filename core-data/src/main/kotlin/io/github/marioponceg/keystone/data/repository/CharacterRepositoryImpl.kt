package io.github.marioponceg.keystone.data.repository

import io.github.marioponceg.keystone.data.mapper.toDomain
import io.github.marioponceg.keystone.data.remote.RaiderIoApi
import io.github.marioponceg.keystone.domain.error.KeystoneResult
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.repository.CharacterRepository

class CharacterRepositoryImpl(private val api: RaiderIoApi) : CharacterRepository {

    override suspend fun getProfile(id: CharacterId): KeystoneResult<CharacterProfile> =
        api.getCharacterProfile(id).toKeystoneResult(notFoundOnBadRequest = true) { it.toDomain(id) }
}
