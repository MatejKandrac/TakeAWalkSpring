package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Event
import com.kandrac.tomco.takeawalkspring.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

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
        where i.user_id = :userId and i.status = 'ACCEPTED';
    """,
        nativeQuery = true
    )
    fun getAllUserEvents(userId: Int): List<Event?>?

    @Query(
        """
        select e.*
        from events e
        join invites i on e.id = i.event_id
        where i.user_id = :userId 
        and e.end_date > :currentTime
        and i.status = 'ACCEPTED';
    """,
        nativeQuery = true
    )
    fun getAllOngoingUserEvents(userId: Int, currentTime: Timestamp): List<Event>?

    @Query(
        """
        select e.*
        from events e
        join invites i on e.id = i.event_id
        where i.user_id = :userId and i.status = 'PENDING';
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

    @Query(
            """
                select u.device_token
                from users u
                join invites i on u.id = i.user_id
                where i.event_id = :eventId and u.device_token is not null;
            """,
            nativeQuery = true
    )
    fun getDeviceTokensForEvent(eventId: Int) : List<String>
    @Query(
        """
        select e.*
        from events e
        join invites i on e.id = i.event_id
        where i.user_id = :userId 
        and e.end_date > :currentTime
        and i.status = 'PENDING';
    """,
        nativeQuery = true
    )
    fun getAllOngoingUserInvites(userId: Int, currentTime: Timestamp): List<Event>?

}