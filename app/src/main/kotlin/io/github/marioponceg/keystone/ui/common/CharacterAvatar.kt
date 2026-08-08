package io.github.marioponceg.keystone.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme

private val AVATAR_SIZE = 64.dp

/**
 * The character's portrait, or a class-coloured initial when there is nothing to show.
 *
 * The fallback reuses [WowClassColors] rather than a generic placeholder asset: it needs no new
 * resource, it is deterministic in screenshot tests, and it matches the class-coloured name
 * sitting next to it in the header. The loading state is the same circle without the initial, so
 * the avatar's footprint never changes and the header cannot reflow when the image lands.
 */
@Composable
fun CharacterAvatar(
    url: String?,
    characterClass: String,
    characterName: String,
    modifier: Modifier = Modifier,
) {
    val classColor = WowClassColors.forClass(characterClass, FoundryTheme.colors.onSurfaceMuted)

    if (url == null) {
        AvatarFallback(modifier = modifier, color = classColor, name = characterName)
        return
    }

    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(AVATAR_SIZE)
            .clip(CircleShape),
        loading = { AvatarFallback(color = classColor, name = null) },
        error = { AvatarFallback(color = classColor, name = characterName) },
    )
}

@Composable
private fun AvatarFallback(color: Color, name: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(AVATAR_SIZE)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        if (name != null) {
            FoundryText(
                text = name.take(1).uppercase(),
                style = FoundryTextStyle.Display,
                color = FoundryTheme.colors.background,
            )
        }
    }
}
