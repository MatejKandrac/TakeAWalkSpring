package com.kandrac.tomco.takeawalkspring.payloadEntities

data class WeatherData(
        val lat: Double,
        val lon: Double,
        val dateStart: String,
        val dateEnd: String
)