package com.fxrates.feature.rates.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.fxrates.feature.rates.detail.RatesDetailUiState.Content
import com.fxrates.feature.rates.detail.model.HistoryItem
import com.fxrates.ui.Strings
import com.fxrates.ui.theme.AppTheme

@Composable
fun RatesDetailUiContent(component: RatesDetailUiComponent, modifier: Modifier = Modifier) {
    val state by component.state.collectAsState()
    RatesDetailUiContent(state = state, onRetry = component::onRetry, onBack = component::onBack, modifier = modifier)
}

@Composable
private fun RatesDetailUiContent(
    state: RatesDetailUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier.fillMaxSize().safeDrawingPadding()) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.spacing.m),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) { Text(Strings.BACK) }
        Text(state.title, style = MaterialTheme.typography.titleLarge)
    }
    when (val content = state.content) {
        is Content.Loading -> {
            Centered { CircularProgressIndicator() }
        }
        is Content.Error -> {
            Centered {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                ) {
                    Text(Strings.DETAIL_ERROR, style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = onRetry) { Text(Strings.RETRY) }
                }
            }
        }
        is Content.Empty -> {
            Centered { Text(Strings.DETAIL_EMPTY) }
        }
        is Content.History -> {
            HistoryContent(content)
        }
    }
}

@Composable
private fun HistoryContent(content: Content.History) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    item {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AppTheme.spacing.m),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.l),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                Text(
                    Strings.detailRate(content.base, content.latest, content.code),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(Strings.detailAsOf(content.latestDate), style = MaterialTheme.typography.bodyMedium)
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                Text(Strings.DETAIL_CHANGE, style = MaterialTheme.typography.titleMedium)
                if (content.comparisonDate == null) {
                    Text(Strings.DETAIL_NEED_OBSERVATIONS, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(
                        Strings.detailPeriod(content.comparisonDate, content.latestDate),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        Strings.detailChange(content.absoluteChange.orEmpty(), content.code),
                        style = MaterialTheme.typography.titleLarge,
                        color = content.trend.color(),
                    )
                    Text(
                        content.percentChange ?: Strings.DETAIL_PERCENT_UNAVAILABLE,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.s)) {
                Text(Strings.DETAIL_HISTORY, style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        Strings.DETAIL_DATE,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(Strings.DETAIL_RATE, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
    items(items = content.items, key = HistoryItem::date) { item ->
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.m, vertical = AppTheme.spacing.s),
        ) {
            Text(item.date, modifier = Modifier.weight(1f))
            Text(item.rate)
        }
        HorizontalDivider()
    }
}

@Composable
private fun RatesDetailUiState.Trend.color() = when (this) {
    RatesDetailUiState.Trend.Up -> AppTheme.colors.positive
    RatesDetailUiState.Trend.Down -> AppTheme.colors.negative
    RatesDetailUiState.Trend.Flat -> MaterialTheme.colorScheme.onSurface
}

@Composable
private fun Centered(content: @Composable () -> Unit) = Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
) {
    content.invoke()
}

@Preview
@Composable
private fun RatesDetailUiContentPreview(
    @PreviewParameter(RatesDetailUiStatePreviewProvider::class) state: RatesDetailUiState,
) = AppTheme {
    RatesDetailUiContent(state = state, onRetry = {}, onBack = {})
}
