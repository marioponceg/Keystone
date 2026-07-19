package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class RegionTest {
    @Test
    fun `EU has correct slug`() {
        assertEquals("eu", Region.EU.slug)
    }

    @Test
    fun `US has correct slug`() {
        assertEquals("us", Region.US.slug)
    }

    @Test
    fun `KR has correct slug`() {
        assertEquals("kr", Region.KR.slug)
    }

    @Test
    fun `TW has correct slug`() {
        assertEquals("tw", Region.TW.slug)
    }
}
