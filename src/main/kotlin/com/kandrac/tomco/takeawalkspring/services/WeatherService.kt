package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.responseEntities.WeatherObj
import com.kandrac.tomco.takeawalkspring.weather.WeatherRemoteResponse
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WeatherService(restTemplate: RestTemplateBuilder) {

    private final val restTemplateBuilder: RestTemplate

    init {
        this.restTemplateBuilder = restTemplate.build()
    }

    fun getWeatherData(lat: Double, lon: Double, date: String) : ResponseEntity<List<WeatherObj>>? {
        val url = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=${lat}" +
                "&longitude=${lon}" +
                "&start_date=$date" +
                "&end_date=$date" +
                "&hourly=weathercode" +
                "&timezone=UTC"
        val data : ResponseEntity<WeatherRemoteResponse>
        try {
            data = restTemplateBuilder.getForEntity(url, WeatherRemoteResponse::class.java)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
        val responseData = mutableListOf<WeatherObj>()
        val hourlyData = data.body!!.hourly;
        for (i in 0 until hourlyData.time.size) {
            responseData.add(WeatherObj(hourlyData.time[i], getWeatherText(hourlyData.weathercode[i])))
        }
        return ResponseEntity.ok(responseData)
    }

    private fun getWeatherText(weatherCode: Int) : String {
        return when (weatherCode) {
            0, 1 -> "Sunny"
            2, 3 -> "Overcast"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65, 66, 67, 80, 81, 82 -> "Rain"
            71, 73, 75, 77, 85, 86 -> "Snow"
            95, 96, 99 -> "Thunderstorm"
            else -> "Unknown"
        }
    }

}