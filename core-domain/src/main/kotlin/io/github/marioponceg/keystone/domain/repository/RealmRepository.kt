package io.github.marioponceg.keystone.domain.repository

import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region

interface RealmRepository {
    /** The region's realms, alphabetically by name. */
    fun realms(region: Region): List<Realm>
}
