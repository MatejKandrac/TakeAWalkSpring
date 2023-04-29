package com.kandrac.tomco.takeawalkspring.payloadEntities

import java.sql.Timestamp

data class EditEventData(
        val start: Timestamp?,
        val end: Timestamp?,
        val newLocations: List<NewLocationData>?,
        val newPeople: List<Int>?,
        val deletedPictures: List<Int>?
)


data class NewLocationData(
        val lat: Double,
        val lon: Double,
        val name: String,
        val order: Int
)