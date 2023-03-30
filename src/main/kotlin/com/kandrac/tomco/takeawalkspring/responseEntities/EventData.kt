package com.kandrac.tomco.takeawalkspring.responseEntities

data class EventData(
    val eventHost: String,
    val eventPeople: List<String>?,
    val eventLocations: List<LocationObj>,
    val eventTime: EventTimeDetailObj,
    val eventStatus: String?
)