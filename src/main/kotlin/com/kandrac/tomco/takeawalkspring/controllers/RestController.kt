package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.responseEntities.MessageObj
import com.kandrac.tomco.takeawalkspring.responseEntities.ProfileObj
import com.kandrac.tomco.takeawalkspring.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/v1"])
class RestController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var eventService: EventService

    @Autowired
    lateinit var inviteService: InviteService

    @Autowired
    lateinit var messageService: MessageService

    @Autowired
    lateinit var locationService: LocationService

    @Autowired
    lateinit var pictureService: PictureService


//    Event Detail
    @GetMapping(value = ["/event/{event-id}/host"])
    fun getEventOwner(@PathVariable("event-id") eventId: Int): String? {
        return eventService.getEventOwner(eventId)
    }

    @GetMapping(value = ["/event/{event-id}/people"])
    fun getEventPeople(@PathVariable("event-id") eventId: Int): List<String>? {
        return inviteService.getInvitePeople(eventId)
    }

    @GetMapping(value = ["/event/{event-id}/description"])
    fun getEventDescription(@PathVariable("event-id") eventId: Int): String? {
        return eventService.getEventDescription(eventId)
    }

    @GetMapping(value = ["/event/{event-id}/status"])
    fun getEventStatus(@PathVariable("event-id") eventId: Int): String? {
        // TODO get userId from JWT token
        return inviteService.getInviteStatus(5, eventId)
    }


//    Profile
    @GetMapping(value = ["/user/{user-id}/profile"])
    fun getUserProfile(@PathVariable("user-id") userId: Int): ProfileObj? {
        return userService.getUserProfile(userId)
    }


//    Chat
    @GetMapping(value = ["/chat/{event-id}/messages"])
    fun getEventMessages(@PathVariable("event-id") eventId: Int): List<MessageObj>? {
        // TODO add pagination
        return messageService.getEventMessages(eventId)
    }


}