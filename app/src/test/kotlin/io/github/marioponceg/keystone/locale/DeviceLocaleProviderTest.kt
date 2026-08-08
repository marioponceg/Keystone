package io.github.marioponceg.keystone.locale

import io.github.marioponceg.keystone.domain.model.ApiLocale
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

// Locale.forLanguageTag rather than Locale.of: the app module compiles against Java 17 source
// level and minSdk 26, and Locale.of is Java 19 / API 36.
class DeviceLocaleProviderTest {

    @Test
    fun `maps the supplied locale to the API locale`() {
        assertEquals(ApiLocale.ES, DeviceLocaleProvider { Locale.forLanguageTag("es-ES") }.current())
        assertEquals(ApiLocale.DE, DeviceLocaleProvider { Locale.forLanguageTag("de-DE") }.current())
    }

    @Test
    fun `distinguishes the two chinese locales by country`() {
        assertEquals(ApiLocale.CN, DeviceLocaleProvider { Locale.forLanguageTag("zh-CN") }.current())
        assertEquals(ApiLocale.TW, DeviceLocaleProvider { Locale.forLanguageTag("zh-TW") }.current())
    }

    @Test
    fun `falls back to english for a language the API does not translate`() {
        assertEquals(ApiLocale.EN, DeviceLocaleProvider { Locale.forLanguageTag("ja-JP") }.current())
    }

    @Test
    fun `re-reads the locale on every call rather than caching it`() {
        var locale = Locale.forLanguageTag("en-GB")
        val provider = DeviceLocaleProvider { locale }

        assertEquals(ApiLocale.EN, provider.current())

        locale = Locale.forLanguageTag("fr-FR")
        assertEquals(ApiLocale.FR, provider.current())
    }
}
