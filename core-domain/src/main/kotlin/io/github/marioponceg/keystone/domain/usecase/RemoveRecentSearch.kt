package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.repository.RecentSearchesRepository

/** Removes a character from the recent search list. */
class RemoveRecentSearch(private val repository: RecentSearchesRepository) {
    suspend operator fun invoke(id: CharacterId) {
        repository.remove(id)
    }
}
