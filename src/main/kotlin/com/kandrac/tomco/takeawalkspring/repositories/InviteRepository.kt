package com.kandrac.tomco.takeawalkspring.repositories


import com.kandrac.tomco.takeawalkspring.entities.Invite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InviteRepository : JpaRepository<Invite, Long> {

    @Query(
        """select u.username
        from invites i
        join users u on u.id = i.user_id
        join events e on e.id = i.event_id
        where e.id = :eventId and i.status = 'ACCEPTED';
    """,
        nativeQuery = true
    )
    fun getEventPeople(eventId: Int): List<String>?


    fun getInviteByUser_IdAndEvent_Id(userId: Int, eventId: Int): Invite?

    fun getInviteByUser_IdAndEvent_IdAndAndStatus(userId: Int, eventId: Int, status: String): Invite?

}