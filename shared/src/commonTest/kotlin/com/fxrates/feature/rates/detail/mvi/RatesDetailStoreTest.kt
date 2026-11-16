package com.fxrates.feature.rates.detail.mvi

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.fxrates.core.utils.Resource
import com.fxrates.domain.model.CurrencyRate
import com.fxrates.domain.model.DatedRate
import com.fxrates.domain.repository.RatesRepository
import com.fxrates.testing.FakeRatesRepository
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class RatesDetailStoreTest : FunSpec({

    val dispatcher = UnconfinedTestDispatcher()
    val observation = DatedRate(LocalDate(2026, 11, 2), "0.9".toBigDecimal())
    beforeSpec {
        isAssertOnMainThreadEnabled = false
        Dispatchers.setMain(dispatcher)
    }
    afterSpec {
        Dispatchers.resetMain()
        isAssertOnMainThreadEnabled = true
    }
    fun createStore(repository: RatesRepository): RatesDetailStore =
        RatesDetailStoreFactory(DefaultStoreFactory(), repository).createStore("USD", "EUR")

    test("initial load uses both currency identifiers and returns history") {
        val repository = FakeRatesRepository().apply { rateHistory = Result.success(listOf(observation)) }
        val store = createStore(repository)
        repository.requestedHistory shouldBe listOf("USD" to "EUR")
        store.state.history shouldBe Resource.Data(listOf(observation))
        store.dispose()
    }

    test("failure and retry load again with the same identifiers") {
        val repository = FakeRatesRepository().apply { rateHistory = Result.failure(IllegalStateException("offline")) }
        val store = createStore(repository)
        store.state.history.shouldBeInstanceOf<Resource.Error>()
        repository.rateHistory = Result.success(listOf(observation))
        store.accept(RatesDetailStore.Intent.OnRetry)
        repository.requestedHistory shouldBe listOf("USD" to "EUR", "USD" to "EUR")
        store.state.history shouldBe Resource.Data(listOf(observation))
        store.dispose()
    }

    test("loading is observable and disposal cancels the request") {
        val pending = CompletableDeferred<List<DatedRate>>()
        var cancelled = false
        val repository = object : RatesRepository {
            override suspend fun getLatestRates(base: String): List<CurrencyRate> = emptyList()
            override suspend fun getRateHistory(base: String, code: String): List<DatedRate> = try {
                pending.await()
            } finally {
                cancelled = true
            }
        }
        val store = createStore(repository)
        store.state.history shouldBe Resource.Loading
        store.dispose()
        cancelled shouldBe true
    }

    test("back publishes a label") {
        runTest(dispatcher) {
            val store = createStore(FakeRatesRepository())
            var label: RatesDetailStore.Label? = null
            val job = launch { label = store.labels.first() }
            store.accept(RatesDetailStore.Intent.OnBack)
            job.join()
            label shouldBe RatesDetailStore.Label.Back
            store.dispose()
        }
    }

})
