package com.example.parkx.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NewParkingSpot(
    val longitude: Double,
    val latitude: Double,
    @SerialName("exchange_time") val exchangeTime: Instant
)

@Serializable
data class ParkingSpot(
    val id: Int,
    @SerialName("user_id") val userId: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("exchange_time") private val exchangeTime: Instant
) {
    fun getExchangeTime(): LocalDateTime {
        return exchangeTime.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
    }
}

@Serializable
data class NewRequest(
    @SerialName("parking_spot_id") val parkingSpotId: Int
)

@Serializable
data class Request(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    @SerialName("exchange_time") private val exchangeTime: Instant,
    @SerialName("owner_name") val ownerName: String,
    @SerialName("owner_surname") val ownerSurname: String,
    @SerialName("requester_name") val requesterName: String,
    @SerialName("requester_surname") val requesterSurname: String,
    val status: RequestStatus
)

@Serializable
enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED;
}