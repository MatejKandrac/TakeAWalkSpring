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

    @Query(
            """
                select count(*)
                from pictures p
                where p.event_id = :eventId ;
            """,
            nativeQuery = true
    )
    fun getLastPictureIndex(eventId: Int) : Int

}