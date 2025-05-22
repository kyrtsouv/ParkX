package com.example.parkx

interface JavaResultCallback<T> {
    fun onSuccess(value: T)
    fun onError(exception: Throwable)
}