package com.fxrates.feature.rates.detail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.fxrates.feature.rates.detail.mvi.RatesDetailStore
import com.fxrates.feature.rates.detail.mvi.RatesDetailStoreFactory
import com.fxrates.ui.Strings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.AssistedFactory
import me.tatarka.inject.annotations.Inject

abstract class RatesDetailUiComponent {

    internal abstract val state: StateFlow<RatesDetailUiState>
    internal abstract fun onRetry()
    internal abstract fun onBack()

}

@Inject
internal class RatesDetailUiComponentDefault(
    @Assisted componentContext: ComponentContext,
    @Assisted base: String,
    @Assisted code: String,
    @Assisted private val callbacks: Callbacks,
    storeFactory: RatesDetailStoreFactory,
    uiStateConverter: RatesDetailUiStateConverter,
    computationDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : RatesDetailUiComponent(), ComponentContext by componentContext {

    @AssistedFactory
    interface Factory {
        fun create(componentContext: ComponentContext, base: String, code: String, callbacks: Callbacks):
            RatesDetailUiComponentDefault
    }

    interface Callbacks {
        fun onBack()
    }

    private val scope = coroutineScope()
    private val store = storeFactory.createStore(base = base, code = code)

    override val state: StateFlow<RatesDetailUiState> = store.states
        .map(uiStateConverter::createUiState)
        .flowOn(computationDispatcher)
        .stateIn(scope, SharingStarted.Eagerly, RatesDetailUiState(title = Strings.detailTitle(base, code)))

    init {
        store.labels.onEach { label ->
            when (label) {
                is RatesDetailStore.Label.Back -> callbacks.onBack()
            }
        }.launchIn(scope)
    }

    override fun onRetry() {
        store.accept(RatesDetailStore.Intent.OnRetry)
    }

    override fun onBack() {
        store.accept(RatesDetailStore.Intent.OnBack)
    }

}
