package com.kandrac.tomco.takeawalkspring.responseEntities

data class EventData(
    val ownerId: Int,
    val eventHost: String,
    val eventName: String,
    val eventLocations: List<LocationObj>,
    val eventTime: EventTimeDetailObj,
    val eventStatus: String?,
    val currentIndex: Int
)