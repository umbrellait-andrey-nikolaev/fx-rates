package com.fxrates.ui

/**
 * Every user-facing string of the app. The app is English-only, so a constant is enough; a
 * string is never written inline in a composable.
 */
object Strings {

    const val RATES_TITLE = "Rates"
    const val RATES_SEARCH_HINT = "Search by code or name"
    const val RATES_NOTHING_FOUND = "Nothing found"
    const val ERROR_TITLE = "Could not load the rates"
    const val RETRY = "Retry"
    const val REFRESH = "Refresh"
    const val BACK = "Back"
    const val DETAIL_EMPTY = "No rates available for this period"
    const val DETAIL_HISTORY = "Last 30 days"
    const val DETAIL_CHANGE = "Change over period"
    const val DETAIL_UNAVAILABLE = "Unavailable"
    const val DETAIL_ERROR = "Could not load rate history"
    const val DETAIL_AS_OF = "As of"
    const val DETAIL_DATE = "Date"
    const val DETAIL_RATE = "Rate"
    const val DETAIL_NEED_OBSERVATIONS = "Change needs at least two observations"
    const val DETAIL_PERCENT_UNAVAILABLE = "Percentage unavailable when the starting rate is zero"

    fun detailRate(base: String, rate: String, code: String): String = "1 $base = $rate $code"

    fun detailAsOf(date: String): String = "$DETAIL_AS_OF $date"

    fun detailChange(change: String, code: String): String = "$change $code"

    fun detailPeriod(start: String, end: String): String = "$start – $end"

    fun detailTitle(base: String, code: String): String = "1 $base in $code"

    fun ratesSubtitle(base: String, date: String): String = "1 $base · $date"

}
