package com.example.parkx.supabase

import com.example.parkx.CoroutineExecutor
import com.example.parkx.JavaResultCallback
import com.example.parkx.ParkingSpot
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseManager {
    lateinit var client: SupabaseClient
        private set

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
    fun signIn(email: String, password: String, callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend({ AuthService.signIn(email, password) }, callback)
    }

    @JvmStatic
    fun signUp(email: String, password: String, callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend({ AuthService.signUp(email, password) }, callback)
    }

    @JvmStatic
    fun signOut(callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend(
            { AuthService.signOut() }, callback
        )
    }


    @JvmStatic
    fun getSpots(callback: JavaResultCallback<List<ParkingSpot>>) {
        CoroutineExecutor.runSuspend({ DatabaseService.getSpots() }, callback)
    }

    @JvmStatic
    fun createParkingSpot(location: String, callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.createSpot(location) }, callback
        )
    }

    @JvmStatic
    fun getMetadata(): String {
        val metadata = client.auth.currentUserOrNull()?.userMetadata
        return metadata.toString()
    }
}