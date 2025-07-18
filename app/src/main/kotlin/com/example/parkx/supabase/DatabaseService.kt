package com.example.parkx.supabase

import com.example.parkx.utils.NewParkingSpot
import com.example.parkx.utils.NewRequest
import com.example.parkx.utils.ParkingSpot
import com.example.parkx.utils.Request
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDateTime
import java.time.ZoneId

// This object contains suspend methods for interacting with the Supabase database
object DatabaseService {

    private fun getInstant(localDateTime: LocalDateTime): kotlinx.datetime.Instant {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toKotlinInstant()
    }

    suspend fun getNearbySpots(
        latitude: Double, longitude: Double, targetTime: LocalDateTime
    ): List<ParkingSpot> {

        val response = SupabaseManager.client.postgrest.rpc(
            function = "get_nearby_spots", parameters = buildJsonObject {
                put("lat", latitude)
                put("long", longitude)
                put("radius_meters", 1000)
                put("target_time", getInstant(targetTime).toString())
            }).decodeList<ParkingSpot>()

        return response
    }

    suspend fun publishSpot(
        latitude: Double, longitude: Double, exchangeTime: LocalDateTime
    ): String {

        val newSpot = NewParkingSpot(
            latitude = latitude, longitude = longitude, exchangeTime = getInstant(exchangeTime)
        )
        SupabaseManager.client.from("parking_spots").insert(newSpot)

        return "Parking spot created"
    }

    suspend fun addRequest(
        parkingSpotId: Int
    ): String {
        val newRequest = NewRequest(parkingSpotId = parkingSpotId)
        SupabaseManager.client.from("requests").insert(newRequest)
        return "Request added"
    }

    suspend fun getRequestsSent(): List<Request> {
        return SupabaseManager.client.postgrest.rpc("get_requests_sent").decodeList<Request>()
    }

    suspend fun getRequestsReceived(): List<Request> {
        return SupabaseManager.client.postgrest.rpc("get_requests_received").decodeList<Request>()
    }

    suspend fun getMyParkingSpots(): List<ParkingSpot> {
        return SupabaseManager.client.postgrest.rpc("get_my_parking_spots")
            .decodeList<ParkingSpot>()
    }

    suspend fun acceptRequest(
        id: Int
    ): String {
        SupabaseManager.client.from("requests").update(
            {
                set("status", "ACCEPTED")
            }
        ) {
            filter {
                eq("id", id)
            }
        }

        return "Request accepted"
    }

    suspend fun rejectRequest(id: Int): String {
        SupabaseManager.client.from("requests").update(
            {
                set("status", "REJECTED")
            }
        ) {
            filter {
                eq("id", id)
            }
        }

        return "Request rejected"
    }
}