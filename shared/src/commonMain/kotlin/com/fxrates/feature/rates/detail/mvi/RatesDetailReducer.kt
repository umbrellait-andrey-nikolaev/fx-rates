package com.fxrates.feature.rates.detail.mvi

import com.fxrates.feature.rates.detail.mvi.RatesDetailStore.Message
import com.fxrates.feature.rates.detail.mvi.RatesDetailStore.State

internal fun State.reduce(message: Message): State = when (message) {
    is Message.UpdateHistory -> copy(history = message.history)
}
