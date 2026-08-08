package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ApiLocaleTest {

    @Test
    fun `maps every directly supported language to its own locale`() {
        assertEquals(ApiLocale.EN, ApiLocale.of("en", "GB"))
        assertEquals(ApiLocale.ES, ApiLocale.of("es", "ES"))
        assertEquals(ApiLocale.DE, ApiLocale.of("de", "DE"))
        assertEquals(ApiLocale.FR, ApiLocale.of("fr", "FR"))
        assertEquals(ApiLocale.IT, ApiLocale.of("it", "IT"))
        assertEquals(ApiLocale.PT, ApiLocale.of("pt", "BR"))
        assertEquals(ApiLocale.RU, ApiLocale.of("ru", "RU"))
        assertEquals(ApiLocale.KO, ApiLocale.of("ko", "KR"))
    }

    @Test
    fun `chinese resolves by country because the language tag alone is ambiguous`() {
        assertEquals(ApiLocale.CN, ApiLocale.of("zh", "CN"))
        assertEquals(ApiLocale.TW, ApiLocale.of("zh", "TW"))
    }

    @Test
    fun `chinese without a recognised country falls back to simplified`() {
        assertEquals(ApiLocale.CN, ApiLocale.of("zh", ""))
        assertEquals(ApiLocale.CN, ApiLocale.of("zh", "SG"))
    }

    @Test
    fun `an unsupported language falls back to english`() {
        // Probed against the live API on 2026-08-08: ja and pl are not translated.
        assertEquals(ApiLocale.EN, ApiLocale.of("ja", "JP"))
        assertEquals(ApiLocale.EN, ApiLocale.of("pl", "PL"))
        assertEquals(ApiLocale.EN, ApiLocale.of("", ""))
    }

    @Test
    fun `matching is case-insensitive because platform tags vary in case`() {
        assertEquals(ApiLocale.ES, ApiLocale.of("ES", "es"))
        assertEquals(ApiLocale.TW, ApiLocale.of("ZH", "tw"))
    }

    @Test
    fun `every slug is the lowercase value the API expects`() {
        assertEquals(
            listOf("en", "es", "de", "fr", "it", "pt", "ru", "ko", "cn", "tw"),
            ApiLocale.entries.map { it.slug },
        )
    }
}
