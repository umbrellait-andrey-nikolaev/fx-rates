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

    fun detailTitle(base: String, code: String): String = "1 $base in $code"

    fun ratesSubtitle(base: String, date: String): String = "1 $base · $date"

}
