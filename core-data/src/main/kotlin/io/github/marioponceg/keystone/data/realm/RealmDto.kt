package io.github.marioponceg.keystone.data.realm

import kotlinx.serialization.Serializable

@Serializable
data class RealmDto(val name: String, val slug: String)
