package io.github.marioponceg.keystone.domain.error

import kotlin.test.Test
import kotlin.test.assertEquals

class KeystoneErrorTest {
    @Test
    fun `CharacterNotFound is a KeystoneError`() {
        val error: KeystoneError = KeystoneError.CharacterNotFound
        assertEquals(KeystoneError.CharacterNotFound, error)
    }

    @Test
    fun `Network is a KeystoneError`() {
        val error: KeystoneError = KeystoneError.Network
        assertEquals(KeystoneError.Network, error)
    }

    @Test
    fun `Unknown is a KeystoneError`() {
        val error: KeystoneError = KeystoneError.Unknown
        assertEquals(KeystoneError.Unknown, error)
    }

    @Test
    fun `CharacterNotFound instances are equal`() {
        assertEquals(KeystoneError.CharacterNotFound, KeystoneError.CharacterNotFound)
    }

    @Test
    fun `Network instances are equal`() {
        assertEquals(KeystoneError.Network, KeystoneError.Network)
    }

    @Test
    fun `Unknown instances are equal`() {
        assertEquals(KeystoneError.Unknown, KeystoneError.Unknown)
    }
}
