package com.kandrac.tomco.takeawalkspring.responseEntities

import java.sql.Timestamp

data class MapEventObj(
    val lat: Double,
    val lon: Double,
    val name: String,
    val dateStart: Timestamp,
    val dateEnd: Timestamp,
    val eventId: Int
)
