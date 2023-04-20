package com.kandrac.tomco.takeawalkspring.services

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.kandrac.tomco.takeawalkspring.entities.Message
import com.kandrac.tomco.takeawalkspring.payloadEntities.MessageData
import com.kandrac.tomco.takeawalkspring.repositories.EventRepository
import com.kandrac.tomco.takeawalkspring.repositories.MessageRepository
import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.MessageObj
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
class MessageService {

    @Autowired
    lateinit var messageRepository: MessageRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var userRepository: UserRepository

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

    fun addEventMessage(eventId: Int, message: MessageData): Boolean {
        val event = eventRepository.findEventById(eventId) ?: return false
        val user = userRepository.findUserById(message.userId) ?: return false
        val newMessage = Message(
            event = event,
            user = user,
            message = message.message,
            sent = Timestamp(System.currentTimeMillis())
        )
        messageRepository.save(newMessage)
        return true
    }

}