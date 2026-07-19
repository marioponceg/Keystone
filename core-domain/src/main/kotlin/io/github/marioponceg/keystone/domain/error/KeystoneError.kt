package io.github.marioponceg.keystone.domain.error

/** Every failure the domain can surface; use cases never throw. */
sealed interface KeystoneError {
    /** Raider.IO does not know the requested character (name/realm/region). */
    data object CharacterNotFound : KeystoneError

    /** The request never produced a response (connectivity, DNS, timeout). */
    data object Network : KeystoneError

    /** Any other failure (server error, malformed payload). */
    data object Unknown : KeystoneError
}
