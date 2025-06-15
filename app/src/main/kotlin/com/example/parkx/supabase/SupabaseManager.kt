package com.example.parkx.supabase

import com.example.parkx.utils.CoroutineExecutor
import com.example.parkx.utils.JavaResultCallback
import com.example.parkx.utils.ParkingSpot
import com.example.parkx.utils.Request
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.JsonObject
import java.time.LocalDateTime

// This function is a Kotlin singleton that is used to handle all interactions with the Supabase backend.
// It initializes the Supabase client and exposes java-friendly static methods for authentication and database operations
// that are used throughout the application.
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

    // Method to sign in a user with email and password.
    @JvmStatic
    fun signIn(email: String, password: String, callback: JavaResultCallback<Unit>) {
        CoroutineExecutor.runSuspend({ AuthService.signIn(email, password) }, callback)
    }

    // Method to sign up a new user with email, password, name, and surname.
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

    // Method to sign out the current user.
    @JvmStatic
    fun signOut(callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend(
            { AuthService.signOut() }, callback
        )
    }


    // Method to get the available parking near a specific location, within ±10 minutes of the given time.
    @JvmStatic
    fun getNearbySpots(
        latitude: Double,
        longitude: Double,
        targetTime: LocalDateTime,
        callback: JavaResultCallback<List<ParkingSpot>>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.getNearbySpots(latitude, longitude, targetTime) },
            callback
        )
    }

    // Method to publish a parking spot at a specific latitude and longitude, with a local date and time.
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

    // Method to add a request for a parking spot by its ID.
    @JvmStatic
    fun addRequest(
        parkingSpotId: Int,
        callback: JavaResultCallback<String>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.addRequest(parkingSpotId) }, callback
        )
    }

    // Method to get the requests sent by the current user.
    @JvmStatic
    fun getRequestsSent(
        callback: JavaResultCallback<List<Request>>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.getRequestsSent() }, callback
        )
    }

    // Method to get the requests made for the user's parking spots.
    @JvmStatic
    fun getRequestsReceived(
        callback: JavaResultCallback<List<Request>>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.getRequestsReceived() }, callback
        )
    }

    // Method to get the parking spots owned by the current user.
    @JvmStatic
    fun getMyParkingSpots(
        callback: JavaResultCallback<List<ParkingSpot>>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.getMyParkingSpots() }, callback
        )
    }

    // Method to get the metadata of the current user ( which includes name and surname ).
    @JvmStatic
    fun getMetadata(): JsonObject? {
        val metadata = client.auth.currentUserOrNull()?.userMetadata
        return metadata
    }

    // Method to accept a request for a parking spot by its ID.
    @JvmStatic
    fun acceptRequest(
        id: Int,
        callback: JavaResultCallback<String>
    ) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.acceptRequest(id) }, callback
        )
    }

    // Method to reject a request for a parking spot by its ID.
    @JvmStatic
    fun rejectRequest(id: Int, callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend(
            { DatabaseService.rejectRequest(id) }, callback
        )
    }
}