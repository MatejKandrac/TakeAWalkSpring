package com.kandrac.tomco.takeawalkspring.payloadEntities

import java.sql.Timestamp

data class CreateEventData(
    val ownerId: Int,
    val description: String?,
    val endDate: Timestamp,
    val start: Timestamp,
    val name: String,
    val users: List<Int>,
    val locations: List<LocationData>
)

data class LocationData(
    val lat: Double,
    val lon: Double,
    val name: String,
    val order: Int
)