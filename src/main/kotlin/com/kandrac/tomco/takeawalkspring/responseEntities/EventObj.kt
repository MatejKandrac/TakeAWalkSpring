package com.kandrac.tomco.takeawalkspring.responseEntities

import java.sql.Timestamp

data class EventObj(
    val name: String,
    val owner: String,
    val start: Timestamp?,
    val end: Timestamp?,
    var peopleGoing: Int?,
    val places: Int?,
    val locations: List<LocationPointObj>,
    val eventId: Int
)