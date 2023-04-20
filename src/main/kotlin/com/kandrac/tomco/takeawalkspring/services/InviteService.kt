package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.Status
import com.kandrac.tomco.takeawalkspring.repositories.InviteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InviteService {

    @Autowired
    lateinit var inviteRepository: InviteRepository

    fun getEventPeople(eventId: Int): List<String>? {
        return inviteRepository.getEventPeople(eventId)
    }

    fun getInviteStatus(userId: Int, eventId: Int): String? {
        val invite = inviteRepository.getInviteByUser_IdAndEvent_Id(userId, eventId) ?: return null
        return invite.status.toString()
    }

    fun acceptUserInvite(userId: Int, eventId: Int): Int? {
        val invite = inviteRepository.getInviteByUser_IdAndEvent_IdAndAndStatus(userId, eventId, Status.PENDING.value) ?: return null
        invite.status = Status.ACCEPTED.value
        val inviteId = inviteRepository.save(invite)
        return inviteId.id
    }

    fun declineUserInvite(userId: Int, eventId: Int): Int? {
        val invite = inviteRepository.getInviteByUser_IdAndEvent_IdAndAndStatus(userId, eventId, Status.PENDING.value) ?: return null
        invite.status = Status.DECLINED.value
        val inviteId = inviteRepository.save(invite)
        return inviteId.id
    }

}