package com.example.parkx.supabase

import com.example.parkx.NewParkingSpot
import com.example.parkx.ParkingSpot
import io.github.jan.supabase.postgrest.from

object DatabaseService {

    suspend fun getSpots(): List<ParkingSpot> {
        val response = SupabaseManager.client.from("Parking Spots")
            .select().decodeList<ParkingSpot>()

        return response
    }

    suspend fun createSpot(location: String): String {
        val newSpot = NewParkingSpot(location)
        val response = SupabaseManager.client.from("Parking Spots")
            .insert(newSpot)
            .decodeSingle<ParkingSpot>()

        return "Parking spot created with ID: ${response.id}"
    }
}