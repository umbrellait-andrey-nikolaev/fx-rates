package com.fxrates.data.repository

import com.fxrates.data.remote.dto.TimeSeriesDto
import com.fxrates.domain.model.CurrencyRate
import com.fxrates.domain.model.DatedRate
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlinx.datetime.LocalDate
import me.tatarka.inject.annotations.Inject

@Inject
class RatesConverter {

    /**
     * Takes the last two dates of [timeSeries] as "latest" and "previous". A currency with no
     * name in [currencies] is still shown, under its code.
     */
    fun toLatestRates(timeSeries: TimeSeriesDto, currencies: Map<String, String>): List<CurrencyRate> {
        val datesDescending = timeSeries.rates.keys.sortedDescending()
        val latestDate = datesDescending.firstOrNull() ?: return emptyList()
        val latest = timeSeries.rates.getValue(latestDate)
        val previous = datesDescending.getOrNull(1)?.let(timeSeries.rates::getValue).orEmpty()
        return latest
            .map { (code, rate) ->
                CurrencyRate(
                    code = code,
                    name = currencies[code] ?: code,
                    date = LocalDate.parse(latestDate),
                    rate = rate.content.toBigDecimal(),
                    previousRate = previous[code]?.content?.toBigDecimal(),
                )
            }
            .sortedBy(CurrencyRate::code)
    }

    fun toRateHistory(timeSeries: TimeSeriesDto, code: String): List<DatedRate> {
        return timeSeries.rates.mapNotNull { (date, rates) ->
            rates[code]?.let { rate -> DatedRate(date = LocalDate.parse(date), rate = rate.content.toBigDecimal()) }
        }.sortedByDescending(DatedRate::date)
    }

}
