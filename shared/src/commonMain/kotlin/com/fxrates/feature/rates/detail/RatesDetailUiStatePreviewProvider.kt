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
                latest = "0.8916",
                latestDate = "2026-11-02",
                absoluteChange = "+0.0100",
                percentChange = "+1.13%",
                items = listOf(HistoryItem("2026-11-02", "0.8916"), HistoryItem("2026-10-30", "0.8816")),
            ),
        ),
    )

}
