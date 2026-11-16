package com.fxrates.core.money

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RateChangeCalculatorTest : FunSpec({

    test("change is calculated relative to the previous rate") {
        val change = RateChangeCalculator.calculate(previous = "100".toBigDecimal(), current = "125".toBigDecimal())

        change.absolute shouldBe "25".toBigDecimal()
        change.percent shouldBe "25".toBigDecimal()
    }

    test("a falling rate has a negative change") {
        val change = RateChangeCalculator.calculate(previous = "10".toBigDecimal(), current = "8".toBigDecimal())

        change.absolute shouldBe "-2".toBigDecimal()
        change.percent shouldBe "-20".toBigDecimal()
    }

    test("a zero previous rate preserves absolute change without a percentage") {
        val change = RateChangeCalculator.calculate(previous = "0".toBigDecimal(), current = "1".toBigDecimal())

        change.absolute shouldBe "1".toBigDecimal()
        change.percent shouldBe null
    }

    test("a zero current rate is a complete loss") {
        val change = RateChangeCalculator.calculate(previous = "5".toBigDecimal(), current = "0".toBigDecimal())

        change.absolute shouldBe "-5".toBigDecimal()
        change.percent shouldBe "-100".toBigDecimal()
    }

    test("an unchanged rate has zero change") {
        val change = RateChangeCalculator.calculate(previous = "0.9".toBigDecimal(), current = "0.9".toBigDecimal())

        change.absolute.isZero() shouldBe true
        change.percent?.isZero() shouldBe true
    }

})
