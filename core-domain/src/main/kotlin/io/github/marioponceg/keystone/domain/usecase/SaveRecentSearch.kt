package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.repository.RecentSearchesRepository

/** Saves a character search to the recent list. */
class SaveRecentSearch(private val repository: RecentSearchesRepository) {
    suspend operator fun invoke(search: RecentSearch) {
        repository.save(search)
    }
}
