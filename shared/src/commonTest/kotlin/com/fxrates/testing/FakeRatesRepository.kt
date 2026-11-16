package com.fxrates.testing

import com.fxrates.domain.model.CurrencyRate
import com.fxrates.domain.model.DatedRate
import com.fxrates.domain.repository.RatesRepository

class FakeRatesRepository : RatesRepository {

    var latestRates: Result<List<CurrencyRate>> = Result.success(emptyList())
    var rateHistory: Result<List<DatedRate>> = Result.success(emptyList())

    val requestedBases = mutableListOf<String>()
    val requestedHistory = mutableListOf<Pair<String, String>>()

    override suspend fun getLatestRates(base: String): List<CurrencyRate> {
        requestedBases += base
        return latestRates.getOrThrow()
    }

    override suspend fun getRateHistory(base: String, code: String): List<DatedRate> {
        requestedHistory += base to code
        return rateHistory.getOrThrow()
    }

}
