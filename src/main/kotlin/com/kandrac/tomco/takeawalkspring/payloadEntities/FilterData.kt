package com.kandrac.tomco.takeawalkspring.payloadEntities

import java.sql.Timestamp

data class FilterData(
    val date: Timestamp?,
    val places: Int,
    val peopleGoing: Int,
    val order: String,
    val showHistory: Boolean
)
