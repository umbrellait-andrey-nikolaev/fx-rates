package com.fxrates.feature.rates.detail.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.fxrates.core.utils.Resource
import com.fxrates.domain.model.DatedRate

internal class RatesDetailStore(
    store: Store<Intent, State, Label>,
) : Store<RatesDetailStore.Intent, RatesDetailStore.State, RatesDetailStore.Label> by store {

    sealed class Intent {

        data object OnRetry : Intent()
        data object OnBack : Intent()

    }

    sealed class Action {

        data class LoadHistory(val base: String, val code: String) : Action()

    }

    sealed class Message {

        data class UpdateHistory(val history: Resource<List<DatedRate>>) : Message()

    }

    sealed class Label {

        data object Back : Label()

    }

    data class State(
        val base: String,
        val code: String,
        val history: Resource<List<DatedRate>> = Resource.Loading,
    )

}
