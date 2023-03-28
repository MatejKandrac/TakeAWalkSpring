package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.repositories.LocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LocationService {

    @Autowired
    lateinit var locationRepository: LocationRepository

}