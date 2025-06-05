package com.example.parkx.supabase

import com.example.parkx.utils.CoroutineExecutor
import com.example.parkx.utils.JavaResultCallback
import com.example.parkx.utils.ParkingSpot
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import java.time.LocalDateTime


object SupabaseManager {
    lateinit var client: SupabaseClient
        private set

    @JvmStatic
    fun init() {
        client = createSupabaseClient(
            supabaseUrl = "https://pymrbdesqdzscvaewbjz.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB5bXJiZGVzcWR6c2N2YWV3Ymp6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDY1MjUxMDIsImV4cCI6MjA2MjEwMTEwMn0.F8sBvUeeoPjaA21_nR3AWIhVMkR10ZfN2iwNQvCV3Co"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    @JvmStatic
    fun signIn(email: String, password: String, callback: JavaResultCallback<Unit>) {
        CoroutineExecutor.runSuspend({ AuthService.signIn(email, password) }, callback)
    }

    @JvmStatic
    fun signUp(
        email: String,
        password: String,
        name: String,
        surname: String,
        callback: JavaResultCallback<Unit>
    ) {
        CoroutineExecutor.runSuspend(
            { AuthService.signUp(email, password, name, surname) },
            callback
        )
    }

    @JvmStatic
    fun signOut(callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend(
            { AuthService.signOut() }, callback
        )
    }


    @JvmStatic
    fun getSpots(
        latitude: Double,
        longitude: Double,
        targetTime: LocalDateTime,
        callback: JavaResultCallback<List<ParkingSpot>>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.getSpots(latitude, longitude, targetTime) },
            callback
        )
    }

    @JvmStatic
    fun publishSpot(
        latitude: Double,
        longitude: Double,
        localDateTime: LocalDateTime,
        callback: JavaResultCallback<String>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.publishSpot(latitude, longitude, localDateTime) }, callback
        )
    }

    @JvmStatic
    fun getMetadata(): String {
        val metadata = client.auth.currentUserOrNull()?.userMetadata
        return metadata.toString()
    }
}