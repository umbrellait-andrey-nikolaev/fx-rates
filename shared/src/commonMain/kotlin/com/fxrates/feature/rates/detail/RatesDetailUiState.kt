package com.fxrates.feature.rates.detail

import androidx.compose.runtime.Immutable
import com.fxrates.core.utils.Empty
import com.fxrates.feature.rates.detail.model.HistoryItem

@Immutable
internal data class RatesDetailUiState(
    val title: String = String.Empty,
    val content: Content = Content.Loading,
) {

    @Immutable
    sealed class Content {
        data object Loading : Content()
        data object Error : Content()
        data object Empty : Content()
        data class History(
            val latest: String,
            val latestDate: String,
            val absoluteChange: String?,
            val percentChange: String?,
            val items: List<HistoryItem>,
        ) : Content()
    }

}
