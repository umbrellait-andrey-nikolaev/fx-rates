package com.fxrates.feature.rates.detail

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.fxrates.feature.rates.detail.RatesDetailUiState.Content
import com.fxrates.feature.rates.detail.model.HistoryItem

internal class RatesDetailUiStatePreviewProvider : PreviewParameterProvider<RatesDetailUiState> {

    override val values: Sequence<RatesDetailUiState> = sequenceOf(
        RatesDetailUiState(title = "1 USD in EUR", content = Content.Loading),
        RatesDetailUiState(title = "1 USD in EUR", content = Content.Error),
        RatesDetailUiState(title = "1 USD in EUR", content = Content.Empty),
        RatesDetailUiState(
            title = "1 USD in EUR",
            content = Content.History(
                base = "USD",
                code = "EUR",
                latest = "0.8916",
                latestDate = "2026-11-02",
                comparisonDate = "2026-10-30",
                absoluteChange = "+0.0100",
                percentChange = "+1.13%",
                items = listOf(HistoryItem("2026-11-02", "0.8916"), HistoryItem("2026-10-30", "0.8816")),
                trend = RatesDetailUiState.Trend.Up,
            ),
        ),
        RatesDetailUiState(
            title = "1 USD in EUR",
            content = Content.History(
                base = "USD", code = "EUR", latest = "0.8816", latestDate = "2026-11-02",
                comparisonDate = "2026-10-30", absoluteChange = "-0.0100", percentChange = "-1.12%",
                items = listOf(HistoryItem("2026-11-02", "0.8816"), HistoryItem("2026-10-30", "0.8916")),
                trend = RatesDetailUiState.Trend.Down,
            ),
        ),
        RatesDetailUiState(
            title = "1 USD in EUR",
            content = Content.History(
                base = "USD", code = "EUR", latest = "0.8916", latestDate = "2026-11-02",
                comparisonDate = null, absoluteChange = null, percentChange = null,
                items = listOf(HistoryItem("2026-11-02", "0.8916")),
            ),
        ),
        RatesDetailUiState(
            title = "1 USD in EUR",
            content = Content.History(
                base = "USD", code = "EUR", latest = "0.8916", latestDate = "2026-11-02",
                comparisonDate = "2026-10-30", absoluteChange = "+0.8916", percentChange = null,
                items = listOf(HistoryItem("2026-11-02", "0.8916"), HistoryItem("2026-10-30", "0.0000")),
                trend = RatesDetailUiState.Trend.Up,
            ),
        ),
    )

}
