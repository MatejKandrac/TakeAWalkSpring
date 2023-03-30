package com.kandrac.tomco.takeawalkspring.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherData(
    val temp: Double,
    val dt_txt: String,
    val main: String
)
