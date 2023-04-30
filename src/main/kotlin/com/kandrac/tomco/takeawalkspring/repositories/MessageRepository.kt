package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Message
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {

    fun findMessagesByEvent_Id(eventId: Int, pageable: PageRequest): List<Message>?

    @Query("select i.user.deviceToken from Invite i where i.event.id = :eventId and i.status = 'ACCEPTED'")
    fun getDeviceTokens(eventId: Int): List<String>

}