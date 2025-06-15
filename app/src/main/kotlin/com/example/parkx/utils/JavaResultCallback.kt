package com.example.parkx.utils

// This interface is a generic callback interface used to handle asynchronous results from kotlin coroutines in a Java-friendly way.
interface JavaResultCallback<T> {
    // Called when the operation completes successfully with the result value.
    fun onSuccess(value: T)

    // Called when the operation fails with an exception.
    fun onError(exception: Throwable)
}