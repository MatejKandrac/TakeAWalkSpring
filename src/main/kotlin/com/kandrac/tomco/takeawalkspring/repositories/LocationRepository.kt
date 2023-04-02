package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Event
import com.kandrac.tomco.takeawalkspring.entities.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<Location, Long> {

    fun findLocationsByEvent_Id(eventId: Int): List<Location>?


    fun findLocationById(locationId: Int) : Location?

    fun findLocationsByEvent(event: Event) : List<Location>?

    @Query("SELECT * FROM locations WHERE event_id = ? AND location_order = 0", nativeQuery = true)
    fun findFirsLocationByEvent(eventId: Int) : Location?

}