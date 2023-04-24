package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.responseEntities.WeatherDateObj
import com.kandrac.tomco.takeawalkspring.weather.WeatherRemoteError
import com.kandrac.tomco.takeawalkspring.weather.WeatherRemoteResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class WeatherService(restTemplate: RestTemplateBuilder) {

    private final val restTemplateBuilder: RestTemplate

    private val logger = LoggerFactory.getLogger(WeatherService::class.java)

    init {
        this.restTemplateBuilder = restTemplate.build()
    }

    fun getWeatherData(lat: Double, lon: Double, dateStart: String, dateEnd: String) : ResponseEntity<List<WeatherDateObj>>? {
        var url = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=${lat}" +
                "&longitude=${lon}" +
                "&start_date=$dateStart" +
                "&end_date=$dateEnd" +
                "&hourly=weathercode" +
                "&timezone=CET"
        var data : ResponseEntity<WeatherRemoteResponse>? = null
        try {
            data = restTemplateBuilder.getForEntity(url, WeatherRemoteResponse::class.java)
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatusCode.valueOf(400)) {
                try {
                    val responseData = e.getResponseBodyAs(WeatherRemoteError::class.java)
                    val newDate = responseData!!.reason.split(" to ")[1]
                    logger.info("Weather request out of range, attempting $newDate as end date")
                    url = "https://api.open-meteo.com/v1/forecast?" +
                            "latitude=${lat}" +
                            "&longitude=${lon}" +
                            "&start_date=$dateStart" +
                            "&end_date=$newDate" +
                            "&hourly=weathercode" +
                            "&timezone=CET"
                    data = restTemplateBuilder.getForEntity(url, WeatherRemoteResponse::class.java)
                } catch (e: Exception) {
                    logger.error(e.message)
                    return ResponseEntity.badRequest().build()
                }
            }
        }
        val responseData = mutableListOf<WeatherDateObj>()
        val hourlyData = data?.body!!.hourly

        var currentObj = WeatherDateObj(dateStart, mutableMapOf())

        for (i in 0 until hourlyData.time.size) {
            val dateData = hourlyData.time[i].split("T")

            if (dateData[0] != currentObj.date) {
                if (currentObj.data.isNotEmpty()) {
                    responseData.add(currentObj)
                }

                currentObj = WeatherDateObj(dateData[0], mutableMapOf())
            }

            currentObj.data.put(dateData[1], getWeatherText(hourlyData.weathercode[i]))
        }
        responseData.add(currentObj)
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
