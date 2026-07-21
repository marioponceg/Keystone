package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.repository.RealmRepository

/** Returns the selectable realms for a region. */
class GetRealms(private val repository: RealmRepository) {
    operator fun invoke(region: Region): List<Realm> = repository.realms(region)
}
