package com.kandrac.tomco.takeawalkspring.repositories


import com.kandrac.tomco.takeawalkspring.entities.Invite
import com.kandrac.tomco.takeawalkspring.responseEntities.EventPeople
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InviteRepository : JpaRepository<Invite, Long> {

    @Query(
        """select u.username
        from invites i
        join users u on u.id = i.user_id
        where i.event_id = :eventId and i.status = 'ACCEPTED';
    """,
        nativeQuery = true
    )
    fun getEventPeople(eventId: Int): List<String>?

    @Query(
        """select u.username, i.status, u.picture
        from invites i
        join users u on u.id = i.user_id
        where i.event_id = :eventId and i.status = 'ACCEPTED' ;
    """,
        nativeQuery = true
    )
    fun getAcceptedEventPeople(eventId: Int): Collection<EventPeople>

    @Query(
        """select u.username, i.status, u.picture
        from invites i
        join users u on u.id = i.user_id
        where i.event_id = :eventId and i.status in ('ACCEPTED', 'PENDING') ;
    """,
        nativeQuery = true
    )
    fun getAllEventPeople(eventId: Int): Collection<EventPeople>

    @Query(
        """select count(id)
        from invites i
        where i.event_id = :eventId and i.status = 'ACCEPTED';
    """,
        nativeQuery = true
    )
    fun countEventPeople(eventId: Int): Int

    fun getInviteByUser_IdAndEvent_Id(userId: Int, eventId: Int): Invite?

}