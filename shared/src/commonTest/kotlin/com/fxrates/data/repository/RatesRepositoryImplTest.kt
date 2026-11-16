package com.fxrates.data.repository

import com.fxrates.data.remote.FrankfurterApi
import com.fxrates.data.remote.dto.TimeSeriesDto
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Instant

class RatesRepositoryImplTest : FunSpec({

    val response = """
        {"base":"USD","start_date":"2026-10-18","end_date":"2026-11-16","rates":{
          "2026-11-13":{"EUR":0.12345678901234567890123456789},
          "2026-11-12":{"JPY":150.123},
          "2026-10-19":{"EUR":0.98765432109876543210987654321}
        }}
    """.trimIndent()

    test("JSON conversion preserves numeric digits for latest and history rates") {
        val series = Json.decodeFromString<TimeSeriesDto>(response)
        val converter = RatesConverter()

        converter.toLatestRates(series, emptyMap()).single().rate shouldBe
            "0.12345678901234567890123456789".toBigDecimal()
        val history = converter.toRateHistory(series, "EUR")
        history.map { rate -> rate.date } shouldBe listOf(LocalDate(2026, 11, 13), LocalDate(2026, 10, 19))
        history.last().rate shouldBe "0.98765432109876543210987654321".toBigDecimal()
        converter.toRateHistory(series, "GBP") shouldBe emptyList()
    }

    test("history requests exactly the inclusive UTC window without a fallback request") {
        runTest {
            val requests = mutableListOf<String>()
            val engine = MockEngine { request ->
                requests += request.url.toString()
                respond(response, headers = headersOf(HttpHeaders.ContentType, "application/json"))
            }
            val client = HttpClient(engine) {
                defaultRequest { url("https://example.com/v1/") }
                install(ContentNegotiation) { json() }
            }
            val clock = object : Clock {
                override fun now(): Instant = Instant.parse("2026-11-16T00:01:00Z")
            }
            try {
                val repository = RatesRepositoryImpl(FrankfurterApi(client), RatesConverter(), clock)
                repository.getRateHistory("USD", "GBP") shouldBe emptyList()
                requests shouldBe listOf("https://example.com/v1/2026-10-18..2026-11-16?base=USD")
            } finally {
                client.close()
            }
        }
    }

})
