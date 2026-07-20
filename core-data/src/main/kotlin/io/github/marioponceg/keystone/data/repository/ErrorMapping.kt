package io.github.marioponceg.keystone.data.repository

import io.github.marioponceg.conduit.result.ConduitResult
import io.github.marioponceg.keystone.domain.error.KeystoneError
import io.github.marioponceg.keystone.domain.error.KeystoneResult

private const val HTTP_BAD_REQUEST = 400

/**
 * Bridges Conduit's transport-level result into the domain result.
 * [notFoundOnBadRequest] is true only for the profile endpoint, where Raider.IO
 * answers 400 for an unknown character.
 */
internal fun <T, R> ConduitResult<T>.toKeystoneResult(
    notFoundOnBadRequest: Boolean,
    transform: (T) -> R,
): KeystoneResult<R> = when (this) {
    is ConduitResult.Success -> KeystoneResult.Success(transform(value))
    is ConduitResult.Failure.Http ->
        if (notFoundOnBadRequest && code == HTTP_BAD_REQUEST) {
            KeystoneResult.Failure(KeystoneError.CharacterNotFound)
        } else {
            KeystoneResult.Failure(KeystoneError.Unknown)
        }
    is ConduitResult.Failure.Network -> KeystoneResult.Failure(KeystoneError.Network)
    is ConduitResult.Failure.Serialization -> KeystoneResult.Failure(KeystoneError.Unknown)
}
