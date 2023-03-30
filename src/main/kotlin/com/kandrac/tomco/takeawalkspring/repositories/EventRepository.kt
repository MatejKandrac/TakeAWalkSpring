package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Event
import com.kandrac.tomco.takeawalkspring.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Long> {

    fun findEventById(eventId: Int): Event?

    fun findEventsByUser(user: User) : List<Event>?

    @Query("SELECT * FROM events WHERE owner_id = ? ORDER BY start LIMIT ?", nativeQuery = true)
    fun findEventsByUserOrderByStart(userId: Int, limit: Int) : List<Event>?
}