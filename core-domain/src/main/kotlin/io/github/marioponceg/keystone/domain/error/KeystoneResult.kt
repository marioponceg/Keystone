package io.github.marioponceg.keystone.domain.error

/** Typed outcome of a domain operation; the domain counterpart of Conduit's ConduitResult. */
sealed interface KeystoneResult<out T> {
    data class Success<out T>(val value: T) : KeystoneResult<T>
    data class Failure(val error: KeystoneError) : KeystoneResult<Nothing>
}

inline fun <T, R> KeystoneResult<T>.map(transform: (T) -> R): KeystoneResult<R> = when (this) {
    is KeystoneResult.Success -> KeystoneResult.Success(transform(value))
    is KeystoneResult.Failure -> this
}

fun <T> KeystoneResult<T>.getOrNull(): T? = when (this) {
    is KeystoneResult.Success -> value
    is KeystoneResult.Failure -> null
}
