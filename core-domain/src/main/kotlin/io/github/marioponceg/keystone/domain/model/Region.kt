package io.github.marioponceg.keystone.domain.model

/** A Raider.IO region; [slug] is the value the API expects in query strings. */
enum class Region(val slug: String) {
    EU("eu"),
    US("us"),
    KR("kr"),
    TW("tw"),
}
