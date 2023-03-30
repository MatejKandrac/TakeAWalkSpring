package com.kandrac.tomco.takeawalkspring.weather

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherResponse(
    val cod: String,
    val list: List<WeatherItem>
)
