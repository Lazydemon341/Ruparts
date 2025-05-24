package com.ruparts.app.core.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Extension function to collect a Flow when the lifecycle is at least in STARTED state.
 * The flow collection will be automatically cancelled when the lifecycle state goes below STARTED.
 * Collection will automatically resume when the lifecycle returns to STARTED state.
 *
 * @param flow The Flow to collect
 * @param action The action to perform on each emitted value
 */
fun <T> Flow<T>.collectWhileStarted(
    lifecycleOwner: LifecycleOwner,
    action: suspend (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { value ->
                action(value)
            }
        }
    }
}
