package com.kandrac.tomco.takeawalkspring.weather

data class WeatherRemoteResponse(
        val hourly: HourlyData
)

data class HourlyData(
        val time: List<String>,
        val weathercode: List<Int>
)