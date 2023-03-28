package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<Location, Long> {

    fun findLocationById(locationId: Int) : Location?

}