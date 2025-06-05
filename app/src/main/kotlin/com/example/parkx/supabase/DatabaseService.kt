package com.example.parkx.supabase

import com.example.parkx.utils.NewParkingSpot
import com.example.parkx.utils.ParkingSpot
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDateTime
import java.time.ZoneId

object DatabaseService {

    private fun getInstant(localDateTime: LocalDateTime): kotlinx.datetime.Instant {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toKotlinInstant()
    }

    suspend fun getSpots(
        latitude: Double,
        longitude: Double,
        targetTime: LocalDateTime = LocalDateTime.now()
    ): List<ParkingSpot> {

        val response = SupabaseManager.client.postgrest.rpc(
            function = "get_nearby_spots",
            parameters = buildJsonObject {
                put("lat", latitude)
                put("long", longitude)
                put("radius_meters", 1000)
                put("target_time", getInstant(targetTime).toString())
            })
            .decodeList<ParkingSpot>()

        return response
    }

    suspend fun publishSpot(
        latitude: Double,
        longitude: Double,
        exchangeTime: LocalDateTime
    ): String {

        val newSpot = NewParkingSpot(
            latitude = latitude, longitude = longitude,
            exchangeTime = getInstant(exchangeTime)
        )
        SupabaseManager.client.from("Parking Spots")
            .insert(newSpot)

        return "Parking spot created"
    }
}