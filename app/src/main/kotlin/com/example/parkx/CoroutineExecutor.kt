package com.example.parkx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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