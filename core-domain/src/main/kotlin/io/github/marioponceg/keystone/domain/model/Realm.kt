package io.github.marioponceg.keystone.domain.model

/** A WoW realm. [name] is the display value ("Tarren Mill"); [slug] is what Raider.IO expects ("tarren-mill"). */
data class Realm(val name: String, val slug: String)
