package io.github.marioponceg.keystone.ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.marioponceg.foundry.components.FoundryCard
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.DungeonRun
import java.util.Locale
import kotlin.time.Duration

/**
 * Deliberately inert to the pointer. A hover border was tried here for symmetry with the recents
 * rows on Home, but those rows are clickable and this card is not: highlighting on hover promised
 * an interaction that does not exist, which is worse than no feedback at all. Restore it only
 * alongside an `onClick`.
 */
@Composable
internal fun DungeonRunCard(run: DungeonRun) {
    val spacing = FoundryTheme.spacing
    val colors = FoundryTheme.colors
    FoundryCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                FoundryText(text = run.dungeonName, style = FoundryTextStyle.BodyStrong)
                FoundryText(
                    text = run.shortName,
                    style = FoundryTextStyle.Caption,
                    color = colors.onSurfaceMuted,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                val filledStars = "★".repeat(run.upgrades)
                val emptyStars = "☆".repeat((MAX_UPGRADES - run.upgrades).coerceAtLeast(0))
                FoundryText(
                    text = "+${run.keystoneLevel} $filledStars$emptyStars",
                    style = FoundryTextStyle.BodyStrong,
                    color = if (run.isTimed) colors.success else colors.danger,
                )
                FoundryText(
                    text = "${formatDuration(run.clearTime)} / ${formatDuration(run.parTime)}",
                    style = FoundryTextStyle.Caption,
                    color = colors.onSurfaceMuted,
                )
                FoundryText(
                    text = "%.1f".format(Locale.US, run.score),
                    style = FoundryTextStyle.Label,
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.xs))
    }
}

private const val MAX_UPGRADES = 3
private const val SECONDS_PER_MINUTE = 60

private fun formatDuration(duration: Duration): String =
    "%d:%02d".format(
        Locale.US,
        duration.inWholeMinutes,
        duration.inWholeSeconds % SECONDS_PER_MINUTE,
    )
