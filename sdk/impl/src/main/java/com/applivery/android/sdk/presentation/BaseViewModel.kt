package com.applivery.android.sdk.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal interface ViewState

internal interface ViewIntent

internal interface ViewAction

internal typealias Reducer<T> = T.() -> T

internal abstract class BaseViewModel<V : ViewState, I : ViewIntent, A : ViewAction>(
) : ViewModel(), ViewModelIntentSender<I> {
    protected abstract val initialViewState: V

    private var pendingAction: A? = null
    private val _viewState: MutableStateFlow<V> by lazy { MutableStateFlow(initialViewState) }
    private val _viewActions: MutableSharedFlow<A> = MutableSharedFlow()

    val viewState: Flow<V> by lazy { _viewState }
    val viewActions: Flow<A> by lazy { _viewActions }

    init {
        _viewActions.subscriptionCount
            .map { it > 0 }
            .distinctUntilChanged()
            .filter { it }
            .onEach {
                pendingAction?.let(::dispatchAction)
                pendingAction = null
            }.launchIn(viewModelScope)
    }

    fun getState(): V = _viewState.value

    protected fun setState(reduce: Reducer<V>) {
        _viewState.update(reduce)
    }

    abstract fun load()

    protected fun dispatchAction(action: A) {
        if (_viewActions.hasSubscribers) {
            viewModelScope.launch { _viewActions.emit(action) }
        } else {
            pendingAction = action
        }
    }

    private val <T> MutableSharedFlow<T>.hasSubscribers: Boolean
        get() = subscriptionCount.value > 0
}

internal interface ViewModelIntentSender<I : ViewIntent> {
    fun sendIntent(intent: I)
}

internal fun <I : ViewIntent> viewModelIntentSender(block: (I) -> Unit): ViewModelIntentSender<I> {
    return object : ViewModelIntentSender<I> {
        override fun sendIntent(intent: I) = block(intent)
    }
}

internal inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
): Job {
    return owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minActiveState) { collect { action(it) } }
    }
}
