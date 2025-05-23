package com.example.parkx.utils

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewParkingSpot(
    val location: String
)

@Serializable
data class ParkingSpot(
    val id: Int,
    @SerialName("user_id") val userId: String,
    val lat: Double,
    val long: Double
)

@Serializable
data class NewRequest(
    @SerialName("parking_spot_id") val parkingSpotId: Int,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("requester_id") val requesterId: String,
)

@Serializable
data class Request(
    val id: Int,
    @SerialName("parking_spot_id") val parkingSpotId: Int,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("requester_id") val requesterId: String,
    val status: String
)

@Serializable
enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED;
}