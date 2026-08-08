package io.github.marioponceg.keystone.ui.character

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import io.github.marioponceg.foundry.components.FoundryButton
import io.github.marioponceg.foundry.components.FoundryButtonStyle
import io.github.marioponceg.foundry.components.FoundryCard
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.DungeonRun
import io.github.marioponceg.keystone.domain.model.RunAffix
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Duration

private val DUNGEON_ICON_SIZE = 36.dp
private val AFFIX_ICON_SIZE = 24.dp
private val ICON_SHAPE = RoundedCornerShape(6.dp)

/**
 * Tapping the card expands it in place: the affixes that week, the date, and (from v0.4's second
 * unit) a link out to the run's page.
 *
 * The hover border is back. It was removed in v0.2 because the card had no `onClick` and
 * highlighting something inert promises an interaction that does not exist — the condition that
 * KDoc set for restoring it ("only alongside an `onClick`") is now met.
 *
 * Stateless on purpose: the layout body owns which run is open, so a screenshot test can
 * capture the expanded card without driving a click, and the choice survives rotation in a
 * `rememberSaveable` rather than dying with the composable.
 */
@Composable
internal fun DungeonRunCard(
    run: DungeonRun,
    expanded: Boolean,
    onToggle: () -> Unit,
    onOpenRun: (String) -> Unit,
) {
    val spacing = FoundryTheme.spacing
    val colors = FoundryTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    FoundryCard(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .then(
                if (isHovered) {
                    Modifier.border(1.dp, colors.accent, FoundryTheme.shapes.lg)
                } else {
                    Modifier
                },
            )
            .semantics { stateDescription = if (expanded) "Expanded" else "Collapsed" }
            .animateContentSize(),
        onClick = onToggle,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DungeonIcon(url = run.iconUrl)
                Column {
                    FoundryText(text = run.dungeonName, style = FoundryTextStyle.BodyStrong)
                    FoundryText(
                        text = run.shortName,
                        style = FoundryTextStyle.Caption,
                        color = colors.onSurfaceMuted,
                    )
                }
            }
            RunStats(run = run, expanded = expanded)
        }
        if (expanded) {
            ExpandedRunDetails(run = run, onOpenRun = onOpenRun)
        }
        Spacer(modifier = Modifier.height(spacing.xs))
    }
}

/** The keystone level, upgrade stars, clear time and score column on the right of the header. */
@Composable
private fun RunStats(run: DungeonRun, expanded: Boolean) {
    val colors = FoundryTheme.colors
    val spacing = FoundryTheme.spacing
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FoundryText(
                text = "%.1f".format(Locale.US, run.score),
                style = FoundryTextStyle.Label,
            )
            // Decorative: the card's stateDescription already announces "Expanded"/"Collapsed".
            FoundryText(
                text = if (expanded) "⌃" else "⌄",
                style = FoundryTextStyle.Label,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }
    }
}

@Composable
private fun ExpandedRunDetails(run: DungeonRun, onOpenRun: (String) -> Unit) {
    // A run with no date, no affixes and no url has nothing to show once expanded: rendering the
    // padded Column below anyway would grow the card a few dp for an empty panel.
    if (run.completedAtEpochMillis == null && run.affixes.isEmpty() && run.url == null) return
    val spacing = FoundryTheme.spacing
    val colors = FoundryTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        run.completedAtEpochMillis?.let { millis ->
            FoundryText(
                text = formatCompletedAt(millis),
                style = FoundryTextStyle.Caption,
                color = colors.onSurfaceMuted,
            )
        }
        run.affixes.forEach { affix ->
            AffixRow(affix = affix)
        }
        run.url?.let { url ->
            FoundryButton(
                text = "View on Raider.IO",
                onClick = { onOpenRun(url) },
                style = FoundryButtonStyle.Secondary,
            )
        }
    }
}

/**
 * English on purpose: `characters/profile` answers 400 to a `locale` parameter, so the affixes
 * embedded in a run cannot be localized. The weekly affixes card on Home can and is.
 */
@Composable
private fun AffixRow(affix: RunAffix) {
    val spacing = FoundryTheme.spacing
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (affix.iconUrl != null) {
            SubcomposeAsyncImage(
                model = affix.iconUrl,
                // The name is written beside it; announcing it twice is noise for TalkBack.
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(AFFIX_ICON_SIZE).clip(ICON_SHAPE),
                loading = {},
                error = {},
            )
        }
        FoundryText(text = affix.name, style = FoundryTextStyle.Caption)
    }
}

@Composable
private fun DungeonIcon(url: String?) {
    if (url == null) return
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(DUNGEON_ICON_SIZE).clip(ICON_SHAPE),
        loading = {},
        error = {},
    )
}

/**
 * Absolute, not relative. "3 weeks ago" reads better and depends on `now()`, which would rot every
 * golden containing it. The formatter takes the locale at call time: a top-level formatter would
 * capture whatever locale the process started with, and tests change it.
 */
private fun formatCompletedAt(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()),
        )

private const val MAX_UPGRADES = 3
private const val SECONDS_PER_MINUTE = 60

private fun formatDuration(duration: Duration): String =
    "%d:%02d".format(
        Locale.US,
        duration.inWholeMinutes,
        duration.inWholeSeconds % SECONDS_PER_MINUTE,
    )
