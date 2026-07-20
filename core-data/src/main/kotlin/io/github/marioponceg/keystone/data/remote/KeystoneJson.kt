package io.github.marioponceg.keystone.data.remote

import kotlinx.serialization.json.Json

/** The one Json configuration for Raider.IO payloads: tolerate everything we don't model. */
val KeystoneJson: Json = Json {
    ignoreUnknownKeys = true
}
