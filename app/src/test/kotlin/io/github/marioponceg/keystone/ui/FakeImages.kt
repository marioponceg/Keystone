package io.github.marioponceg.keystone.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.setSingletonImageLoaderFactory
import coil3.test.FakeImageLoaderEngine

/** The single flat colour standing in for every remote image in previews and goldens. */
val FAKE_IMAGE_COLOR = Color(0xFF4C6EF5)

@OptIn(ExperimentalCoilApi::class)
private val previewHandler = AsyncImagePreviewHandler {
    ColorImage(FAKE_IMAGE_COLOR.toArgb())
}

/**
 * Makes every remote image resolve to [FAKE_IMAGE_COLOR], so goldens never depend on the network.
 *
 * Two mechanisms, because one does not cover both callers:
 *
 * - [FakeImageLoaderEngine] replaces the singleton loader. This is what actually runs under
 *   Robolectric, where `LocalInspectionMode` is false and Coil takes its normal path.
 * - [LocalAsyncImagePreviewHandler] covers Compose `@Preview`, where `AsyncImage` short-circuits
 *   on `LocalInspectionMode` and would otherwise draw nothing at all.
 *
 * Both are wired here, to the same colour, so a preview and the screenshot test that is supposed
 * to mirror it cannot disagree.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun WithFakeImages(content: @Composable () -> Unit) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(FakeImageLoaderEngine.Builder().default(ColorImage(FAKE_IMAGE_COLOR.toArgb())).build()) }
            .build()
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        content()
    }
}
