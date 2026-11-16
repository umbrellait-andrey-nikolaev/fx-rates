package com.fxrates.domain.repository

import com.fxrates.domain.model.CurrencyRate
import com.fxrates.domain.model.DatedRate

interface RatesRepository {

    /** The latest rates of every supported currency against [base], sorted by code. */
    suspend fun getLatestRates(base: String): List<CurrencyRate>

    /** Available observations in the last 30 UTC calendar days, newest first. */
    suspend fun getRateHistory(base: String, code: String): List<DatedRate>

}
