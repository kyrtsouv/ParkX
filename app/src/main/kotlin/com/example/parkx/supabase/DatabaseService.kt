package com.example.parkx.supabase

import com.example.parkx.utils.NewParkingSpot
import com.example.parkx.utils.ParkingSpot
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object DatabaseService {

    suspend fun getSpots(lat: Double, long: Double): List<ParkingSpot> {
        val response = SupabaseManager.client.postgrest.rpc(
            function = "get_nearby_spots",
            parameters = buildJsonObject {
                put("lat", lat)
                put("long", long)
                put("radius_meters", 1000)
            })
            .decodeList<ParkingSpot>()

        return response
    }

    suspend fun publishSpot(location: String): String {
        val newSpot = NewParkingSpot(location)
        SupabaseManager.client.from("Parking Spots")
            .insert(newSpot)

        return "Parking spot created"
    }
}