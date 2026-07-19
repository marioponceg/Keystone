package io.github.marioponceg.keystone.domain.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KeystoneResultTest {

    @Test
    fun `map transforms only success`() {
        assertEquals(
            KeystoneResult.Success(2),
            KeystoneResult.Success(1).map { it + 1 },
        )
        val failure: KeystoneResult<Int> = KeystoneResult.Failure(KeystoneError.Network)
        assertEquals(failure, failure.map { it + 1 })
    }

    @Test
    fun `getOrNull returns value on success and null on failure`() {
        assertEquals(1, KeystoneResult.Success(1).getOrNull())
        assertNull(KeystoneResult.Failure(KeystoneError.Unknown).getOrNull())
    }
}
