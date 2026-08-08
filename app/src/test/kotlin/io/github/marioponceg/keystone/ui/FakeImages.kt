package io.github.marioponceg.keystone.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.test.FakeImageLoaderEngine

/** The single flat colour standing in for every remote image in previews and goldens. */
val FAKE_IMAGE_COLOR = Color(0xFF4C6EF5)

@OptIn(ExperimentalCoilApi::class)
private val previewHandler = AsyncImagePreviewHandler {
    ColorImage(FAKE_IMAGE_COLOR.toArgb())
}

/**
 * Installs the fake loader exactly once per process.
 *
 * `setSingletonImageLoaderFactory` cannot be used here: it delegates to `setSafe`, which throws
 * once the singleton has been created, and Robolectric runs every test class in one JVM — so the
 * second class to compose would fail with "the singleton image loader has already been created".
 * `setUnsafe` overwrites instead, which is what a test process wants, and the `lazy` keeps it to a
 * single call regardless of how many tests ask for it.
 */
@OptIn(DelicateCoilApi::class)
private val installFakeLoader: Unit by lazy {
    val engine = FakeImageLoaderEngine.Builder()
        .default(ColorImage(FAKE_IMAGE_COLOR.toArgb()))
        .build()
    SingletonImageLoader.setUnsafe { context ->
        ImageLoader.Builder(context).components { add(engine) }.build()
    }
}

/**
 * Makes every remote image resolve to [FAKE_IMAGE_COLOR], so goldens never depend on the network.
 *
 * Two mechanisms, because neither alone covers both callers:
 *
 * - [FakeImageLoaderEngine] replaces the singleton loader. This is what actually runs under
 *   Robolectric, where `LocalInspectionMode` is false and Coil takes its normal path.
 * - [LocalAsyncImagePreviewHandler] covers Compose `@Preview`, where `AsyncImage` short-circuits
 *   on `LocalInspectionMode` and would otherwise draw nothing at all.
 *
 * Both are wired to the same colour, so a preview and the screenshot test that is supposed to
 * mirror it cannot disagree.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun WithFakeImages(content: @Composable () -> Unit) {
    installFakeLoader
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        content()
    }
}
