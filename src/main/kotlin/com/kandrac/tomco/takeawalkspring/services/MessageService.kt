package com.kandrac.tomco.takeawalkspring.services

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.kandrac.tomco.takeawalkspring.entities.Message
import com.kandrac.tomco.takeawalkspring.payloadEntities.MessageData
import com.kandrac.tomco.takeawalkspring.repositories.EventRepository
import com.kandrac.tomco.takeawalkspring.repositories.MessageRepository
import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.MessageObj
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.ZonedDateTime

@Service
class MessageService {

    @Autowired
    lateinit var messageRepository: MessageRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var userRepository: UserRepository

    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    fun getEventMessages(eventId: Int, pageNumber: Int, pageSize: Int): List<MessageObj>? {

        val page = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "sent"))

        val messages: List<Message> = messageRepository.findMessagesByEvent_Id(eventId, page)?.reversed() ?: return null

        val resultMessages = mutableListOf<MessageObj>()



        for (msg in messages) {
            resultMessages.add(MessageObj(
                id = msg.id!!,
                message = msg.message!!,
                sent = msg.sent!!,
                userName = msg.user!!.username!!,
                userId = msg.user!!.id!!,
                profilePicture = msg.user!!.picture
            ))
        }

        return resultMessages
    }

    fun addEventMessage(eventId: Int, message: MessageData): Int? {
        val event = eventRepository.findEventById(eventId) ?: return null
        val user = userRepository.findUserById(message.userId) ?: return null
        val newMessage = Message(
            event = event,
            user = user,
            message = message.message,
            sent = Timestamp.from(ZonedDateTime.now().minusHours(2).toInstant())
        )
        val newDbMessage = messageRepository.save(newMessage)

        val tokens = eventRepository.getDeviceTokensForEvent(user.id!!, eventId)

        val ownerToken = eventRepository.getOwnerDeviceToken(eventId)
        val allTokens = mutableListOf<String>()

        allTokens.addAll(tokens)
        if (ownerToken != null) {
            allTokens.add(ownerToken)
        }

        logger.info(allTokens.toString())

        if (allTokens.isNotEmpty()) {
            val remoteMessage = MulticastMessage
                .builder()
                .putAllData(mapOf(
                    "notification_title" to "${event.name}",
                    "notification_content" to "New message from ${user.username}",
                    "event_id" to "$eventId",
                    "message_username" to "${user.username}",
                    "message_picture" to "${user.picture}",
                    "message_userId" to "${user.id}",
                    "message" to message.message,
                    "message_id" to "${newDbMessage.id}",
                    "message_sent" to "${newDbMessage.sent}"
                ))
                .addAllTokens(allTokens)
                .build()
            FirebaseMessaging.getInstance().sendMulticast(remoteMessage)
        }

        return newDbMessage.id
    }

    fun getDeviceTokens(eventId: Int): List<String> {
        return messageRepository.getDeviceTokens(eventId)
    }

}