package io.github.marioponceg.keystone.domain.model

/** Identity of a character as Raider.IO sees it: region + realm + name. */
data class CharacterId(
    val region: Region,
    val realm: String,
    val name: String,
)
