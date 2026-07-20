package io.github.marioponceg.keystone.domain.usecase

import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.repository.RecentSearchesRepository
import kotlinx.coroutines.flow.Flow

/** Observes the list of recent character searches. */
class ObserveRecentSearches(private val repository: RecentSearchesRepository) {
    operator fun invoke(): Flow<List<RecentSearch>> = repository.observe()
}
