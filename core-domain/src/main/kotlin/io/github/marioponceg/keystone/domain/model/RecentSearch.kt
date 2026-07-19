package io.github.marioponceg.keystone.domain.model

/** A successfully resolved search, remembered locally. */
data class RecentSearch(
    val id: CharacterId,
    val searchedAtEpochMillis: Long,
)

const val MAX_RECENT_SEARCHES: Int = 10

/**
 * Returns this list with [search] first: an existing entry with the same [CharacterId]
 * is replaced rather than duplicated, and the result never exceeds [max] entries.
 */
fun List<RecentSearch>.push(search: RecentSearch, max: Int = MAX_RECENT_SEARCHES): List<RecentSearch> =
    buildList {
        add(search)
        addAll(this@push.filterNot { it.id == search.id })
    }.take(max)
