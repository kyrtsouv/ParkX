package com.example.parkx.utils

interface JavaResultCallback<T> {
    fun onSuccess(value: T)
    fun onError(exception: Throwable)
}