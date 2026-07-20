package io.github.marioponceg.keystone.data

fun fixture(name: String): String =
    checkNotNull(object {}.javaClass.getResourceAsStream("/fixtures/$name")) {
        "Missing fixture $name"
    }.bufferedReader().readText()
