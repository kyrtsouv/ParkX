package com.example.parkx.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// This object provides a function to run a suspend function in an IO coroutine and handles
// the result or error back in the MAIN coroutine using a JavaResultCallback.
// This allows us to execute suspend functions from Java code for backend operations.
object CoroutineExecutor {
    @JvmStatic
    fun <T> runSuspend(
        block: suspend () -> T, callback: JavaResultCallback<T>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val value = block()
                withContext(Dispatchers.Main) {
                    callback.onSuccess(value)
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    callback.onError(e)
                }
            }
        }
    }
}