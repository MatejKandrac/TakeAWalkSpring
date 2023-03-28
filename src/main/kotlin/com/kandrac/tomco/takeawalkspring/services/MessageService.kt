package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.Message
import com.kandrac.tomco.takeawalkspring.repositories.MessageRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.MessageObj
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageService {

    @Autowired
    lateinit var messageRepository: MessageRepository

    fun getEventMessages(eventId: Int): List<MessageObj>? {
        val messages: List<Message> = messageRepository.findMessagesByEvent_Id(eventId) ?: return null
        val resultMessages = mutableListOf<MessageObj>()

        for (msg in messages) {
            resultMessages.add(MessageObj(
                id = msg.id!!,
                message = msg.message!!,
                sent = msg.sent!!,
                userName = msg.user!!.username!!,
                profilePicture = msg.user!!.picture
            ))
        }

        return resultMessages
    }

}