package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Long> {

    fun findEventById(eventId: Int): Event?

    @Query(
        """
        select e.*
        from events e
        join invites i on e.id = i.event_id
        where i.user_id = :userId and i.status = 'Accepted';
    """,
        nativeQuery = true
    )
    fun getAllUserEvents(userId: Int): List<Event?>?

    @Query(
        """
        select e.*
        from events e
        join invites i on e.id = i.event_id
        where i.user_id = :userId and i.status = 'Pending';
    """,
        nativeQuery = true
    )
    fun getAllUserInvites(userId: Int): List<Event>?

}