package com.fxrates.feature.rates.detail.mvi

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.fxrates.domain.repository.RatesRepository
import com.fxrates.feature.rates.detail.mvi.RatesDetailStore.Action
import com.fxrates.feature.rates.detail.mvi.RatesDetailStore.State
import me.tatarka.inject.annotations.Inject

@Inject
internal class RatesDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val repository: RatesRepository,
) {

    fun createStore(base: String, code: String): RatesDetailStore {
        val store = storeFactory.create(
            name = "RatesDetailStore",
            initialState = State(base = base, code = code),
            bootstrapper = SimpleBootstrapper(Action.LoadHistory(base = base, code = code)),
            executorFactory = { RatesDetailExecutor(repository = repository) },
            reducer = { message -> reduce(message) },
        )
        return RatesDetailStore(store)
    }

}
