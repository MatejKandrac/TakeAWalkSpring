package com.kandrac.tomco.takeawalkspring.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherItem(
    val main: WeatherData
)
