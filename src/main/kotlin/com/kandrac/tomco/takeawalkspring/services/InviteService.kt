package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.repositories.InviteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InviteService {

    @Autowired
    lateinit var inviteRepository: InviteRepository

    fun getInvitePeople(eventId: Int): List<String>? {
        return inviteRepository.getInvitePeople(eventId)
    }

    fun getInviteStatus(userId: Int, eventId: Int): String? {
        val invite = inviteRepository.getInviteByUser_IdAndEvent_Id(userId, eventId) ?: return null
        return invite.status
    }


}