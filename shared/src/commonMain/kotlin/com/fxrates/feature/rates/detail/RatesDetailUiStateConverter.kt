package com.fxrates.feature.rates.detail

import com.fxrates.core.format.NumberFormatter
import com.fxrates.core.money.RateChangeCalculator
import com.fxrates.core.utils.Resource
import com.fxrates.domain.model.DatedRate
import com.fxrates.feature.rates.detail.RatesDetailUiState.Content
import com.fxrates.feature.rates.detail.model.HistoryItem
import com.fxrates.feature.rates.detail.mvi.RatesDetailStore
import com.fxrates.ui.Strings
import me.tatarka.inject.annotations.Inject

@Inject
internal class RatesDetailUiStateConverter {

    fun createUiState(state: RatesDetailStore.State): RatesDetailUiState {
        val content = when (val history = state.history) {
            is Resource.Loading -> Content.Loading
            is Resource.Error -> Content.Error
            is Resource.Data -> createContent(history.value, state.base, state.code)
        }
        return RatesDetailUiState(title = Strings.detailTitle(state.base, state.code), content = content)
    }

    private fun createContent(history: List<DatedRate>, base: String, code: String): Content {
        val ordered = history.sortedByDescending(DatedRate::date)
        val latest = ordered.firstOrNull() ?: return Content.Empty
        val change = if (ordered.size >= 2) {
            RateChangeCalculator.calculate(previous = ordered.last().rate, current = latest.rate)
        } else {
            null
        }
        val absoluteChange = change?.absolute?.let(NumberFormatter::formatSignedRate)
        val trend = when {
            absoluteChange?.startsWith("+") == true -> RatesDetailUiState.Trend.Up
            absoluteChange?.startsWith("-") == true -> RatesDetailUiState.Trend.Down
            else -> RatesDetailUiState.Trend.Flat
        }
        return Content.History(
            base = base,
            code = code,
            latest = NumberFormatter.formatRate(latest.rate),
            latestDate = latest.date.toString(),
            comparisonDate = ordered.last().date.toString().takeIf { ordered.size >= 2 },
            absoluteChange = absoluteChange,
            percentChange = change?.percent?.let(NumberFormatter::formatSignedPercent),
            items = ordered.map { rate -> HistoryItem(rate.date.toString(), NumberFormatter.formatRate(rate.rate)) },
            trend = trend,
        )
    }

}
