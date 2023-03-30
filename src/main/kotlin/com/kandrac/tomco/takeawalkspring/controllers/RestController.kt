package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.responseEntities.EventData
import com.kandrac.tomco.takeawalkspring.responseEntities.EventObj
import com.kandrac.tomco.takeawalkspring.responseEntities.MessageObj
import com.kandrac.tomco.takeawalkspring.responseEntities.ProfileObj
import com.kandrac.tomco.takeawalkspring.security.UserSecurity
import com.kandrac.tomco.takeawalkspring.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
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


//    Invitations and MyEvents

    @GetMapping(value = ["/events/{user-id}/invitations"])
    fun getUserInvitations(@PathVariable("user-id") userId: Int): List<EventObj>? {
        // TODO pagination
        val invites = eventService.getAllUserInvites(userId) ?: return null

        invites.forEach { invite ->
            val peopleNum = inviteService.getEventPeople(invite.eventId)?.size
            invite.peopleGoing = peopleNum
        }

        return invites
    }

    @GetMapping(value = ["/events/{user-id}/my-events"])
    fun getUserEvents(@PathVariable("user-id") userId: Int): List<EventObj>? {
        // TODO pagination
        val events = eventService.getAllUserEvents(userId) ?: return emptyList()

        events.forEach { event ->
            val peopleNum = inviteService.getEventPeople(event.eventId)?.size
            event.peopleGoing = peopleNum
        }

        return events
    }


    //    Event Detail
    @GetMapping(value = ["/event/{event-id}/host"])
    fun getEventOwner(@PathVariable("event-id") eventId: Int): String? {
        return eventService.getEventOwner(eventId)
    }

    @GetMapping(value = ["/event/{event-id}/people"])
    fun getEventPeople(@PathVariable("event-id") eventId: Int): List<String>? {
        return inviteService.getEventPeople(eventId)
    }

    @GetMapping(value = ["/event/{event-id}/description"])
    fun getEventDescription(@PathVariable("event-id") eventId: Int): String? {
        return eventService.getEventDescription(eventId)
    }

    @GetMapping(value = ["/event/{event-id}/status"])
    fun getEventStatus(@PathVariable("event-id") eventId: Int, auth: Authentication): String? {
        val userId = (auth.principal as UserSecurity).id
        return inviteService.getInviteStatus(userId.toInt(), eventId)
    }

    @GetMapping(value = ["/event/{event-id}/data"])
    fun getEventData(@PathVariable("event-id") eventId: Int, auth: Authentication): EventData {
        val userId = (auth.principal as UserSecurity).id

        val eventHost = eventService.getEventOwner(eventId)
        val eventPeople = inviteService.getEventPeople(eventId)
        val eventLocations = locationService.getEventLocations(eventId)
        val eventTime = eventService.getEventTimeDetail(eventId)
        val eventStatus = inviteService.getInviteStatus(userId.toInt(), eventId)

        return EventData(
            eventHost = eventHost!!,
            eventPeople = eventPeople,
            eventLocations = eventLocations,
            eventTime = eventTime,
            eventStatus = eventStatus
        )
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