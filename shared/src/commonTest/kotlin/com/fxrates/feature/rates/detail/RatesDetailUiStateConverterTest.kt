package com.fxrates.feature.rates.detail

import com.fxrates.core.utils.Resource
import com.fxrates.domain.model.DatedRate
import com.fxrates.feature.rates.detail.RatesDetailUiState.Content
import com.fxrates.feature.rates.detail.mvi.RatesDetailStore
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.datetime.LocalDate

class RatesDetailUiStateConverterTest : FunSpec({

    val converter = RatesDetailUiStateConverter()
    fun rate(day: Int, value: String) = DatedRate(LocalDate(2026, 11, day), value.toBigDecimal())
    fun state(vararg rates: DatedRate) = RatesDetailStore.State("USD", "EUR", Resource.Data(rates.toList()))

    test("history is ordered newest first with latest date and signed period change") {
        val ui = converter.createUiState(state(rate(1, "1"), rate(3, "1.25"), rate(2, "1.1")))
        val content = ui.content.shouldBeInstanceOf<Content.History>()

        ui.title shouldBe "1 USD in EUR"
        content.base shouldBe "USD"
        content.code shouldBe "EUR"
        content.latest shouldBe "1.2500"
        content.latestDate shouldBe "2026-11-03"
        content.comparisonDate shouldBe "2026-11-01"
        content.items.map { item -> item.date } shouldBe listOf("2026-11-03", "2026-11-02", "2026-11-01")
        content.absoluteChange shouldBe "+0.2500"
        content.percentChange shouldBe "+25.00%"
    }

    test("an empty history is distinct from loading and error") {
        converter.createUiState(state()).content shouldBe Content.Empty
        converter.createUiState(RatesDetailStore.State("USD", "EUR")).content shouldBe Content.Loading
        converter.createUiState(RatesDetailStore.State("USD", "EUR", Resource.Error(Exception()))).content shouldBe
            Content.Error
    }

    test("a single observation has no period change") {
        val content = converter.createUiState(state(rate(1, "1.25"))).content.shouldBeInstanceOf<Content.History>()
        content.latestDate shouldBe "2026-11-01"
        content.comparisonDate shouldBe null
        content.absoluteChange shouldBe null
        content.percentChange shouldBe null
    }

    test("a zero starting rate keeps the absolute change and leaves percent unavailable") {
        val content = converter.createUiState(state(rate(1, "0"), rate(2, "1")))
            .content.shouldBeInstanceOf<Content.History>()
        content.comparisonDate shouldBe "2026-11-01"
        content.absoluteChange shouldBe "+1.0000"
        content.percentChange shouldBe null
    }

    test("falling and rounded-zero changes are formatted correctly") {
        val falling = converter.createUiState(state(rate(1, "2"), rate(2, "1")))
            .content.shouldBeInstanceOf<Content.History>()
        falling.absoluteChange shouldBe "-1.0000"
        falling.percentChange shouldBe "-50.00%"
        val rounded = converter.createUiState(state(rate(1, "1"), rate(2, "1.000001")))
            .content.shouldBeInstanceOf<Content.History>()
        rounded.absoluteChange shouldBe "0.0000"
        rounded.percentChange shouldBe "0.00%"
    }

})
