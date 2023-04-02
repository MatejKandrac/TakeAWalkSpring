package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.repositories.LocationRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.LocationObj
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LocationService {

    @Autowired
    lateinit var locationRepository: LocationRepository

    fun getEventLocations(eventId: Int): List<LocationObj> {
        val locations = locationRepository.findLocationsByEvent_Id(eventId) ?: emptyList()
        val locationObjects = mutableListOf<LocationObj>()

        for (location in locations) {
            locationObjects.add(
                LocationObj(
                    name = location.name,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    order = location.locationOrder,
                    visited = location.visited
                )
            )
        }

        return locationObjects
    }

    fun updateLocation(eventId: Int, id: Int): Boolean {
        val location = locationRepository.findLocationById(id) ?: return false
        location.visited = true
        locationRepository.save(location)
        return true
    }
}