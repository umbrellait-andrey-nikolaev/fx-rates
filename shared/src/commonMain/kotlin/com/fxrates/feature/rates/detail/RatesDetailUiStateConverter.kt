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
            is Resource.Data -> createContent(history.value)
        }
        return RatesDetailUiState(title = Strings.detailTitle(state.base, state.code), content = content)
    }

    private fun createContent(history: List<DatedRate>): Content {
        val ordered = history.sortedByDescending(DatedRate::date)
        val latest = ordered.firstOrNull() ?: return Content.Empty
        val change = if (ordered.size >= 2) {
            RateChangeCalculator.calculate(previous = ordered.last().rate, current = latest.rate)
        } else {
            null
        }
        return Content.History(
            latest = NumberFormatter.formatRate(latest.rate),
            latestDate = latest.date.toString(),
            absoluteChange = change?.absolute?.let(NumberFormatter::formatSignedRate),
            percentChange = change?.percent?.let(NumberFormatter::formatSignedPercent),
            items = ordered.map { rate -> HistoryItem(rate.date.toString(), NumberFormatter.formatRate(rate.rate)) },
        )
    }

}
