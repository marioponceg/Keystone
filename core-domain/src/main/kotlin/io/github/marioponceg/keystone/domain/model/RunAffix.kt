package io.github.marioponceg.keystone.domain.model

/**
 * One affix a run was played under.
 *
 * Deliberately not [Affix]. That one comes from the weekly affixes endpoint: it is localized and
 * carries a description but no icon. This one is embedded in the character profile, whose endpoint
 * rejects the `locale` parameter, so it is English-only and carries an icon instead. Merging the
 * two would make half the fields nullable on both sides and force callers to guess which half is
 * populated.
 */
data class RunAffix(
    val name: String,
    val iconUrl: String?,
)
