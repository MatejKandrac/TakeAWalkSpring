package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.responseEntities.EventData
import com.kandrac.tomco.takeawalkspring.responseEntities.EventObj
import com.kandrac.tomco.takeawalkspring.entities.Picture
import com.kandrac.tomco.takeawalkspring.payloadEntities.CreateEventData
import com.kandrac.tomco.takeawalkspring.payloadEntities.MessageData
import com.kandrac.tomco.takeawalkspring.payloadEntities.ProfileEditData
import com.kandrac.tomco.takeawalkspring.responseEntities.MapEventObj
import com.kandrac.tomco.takeawalkspring.responseEntities.MessageObj
import com.kandrac.tomco.takeawalkspring.responseEntities.ProfileObj
import com.kandrac.tomco.takeawalkspring.security.UserSecurity
import com.kandrac.tomco.takeawalkspring.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

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

//    Profile edit
    @PutMapping(value = ["/user/{user-id}/edit"])
    fun editUserProfile(@PathVariable("user-id") userId: Int, @RequestBody data: ProfileEditData): ResponseEntity<String> {
        return if (userService.updateUserProfile(userId, data))
            ResponseEntity.status(HttpStatus.OK).body("Success") else ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found")
    }

    @PutMapping(value = ["/user/{user-id}/profile-picture"])
    fun editUserProfile(@PathVariable("user-id") userId: Int, file: MultipartFile): ResponseEntity<String> {
        return if (userService.updateUserProfileImage(userId, file.bytes))
            ResponseEntity.ok("Success") else
                ResponseEntity.badRequest().body("User not found")
    }

    //    Chat
    @GetMapping(value = ["/chat/{event-id}/messages"])
    fun getEventMessages(@PathVariable("event-id") eventId: Int): List<MessageObj>? {
        // TODO add pagination
        return messageService.getEventMessages(eventId)
    }

    @PostMapping(value = ["chat/{event-id}/message"])
    fun postEventMessage(
        @PathVariable("event-id") eventId: Int,
        @RequestBody message: MessageData) : ResponseEntity<String> {
        return if (messageService.addEventMessage(eventId, message))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

//    Event Progress
    @GetMapping(value = ["event/{event-id}/pictures"])
    fun getEventPictures(@PathVariable("event-id") eventId: Int): List<String?>? {
        return pictureService.getEventPictures(eventId)
    }

    @PostMapping(value = ["event/{event-id}/picture"])
    fun postEventPicture(
        @PathVariable("event-id") eventId: Int,
        file: MultipartFile
    ) : ResponseEntity<String> {
        return if (pictureService.postEventPicture(eventId, file))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }


    //TODO consider deleting by id, no event id needed
    @DeleteMapping(value = ["event/{event-id}/picture"])
    fun deleteEventPicture(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ) : ResponseEntity<String> {
        if (!data.containsKey("picture_id")) return ResponseEntity.badRequest().body("No picture id provided")
        return if (pictureService.deleteEventImage(eventId, data["picture_id"]!! as Int))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

    @PutMapping(value = ["event/{event-id}/description"])
    fun updateEventDescription(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ) : ResponseEntity<String> {
        if (!data.containsKey("description")) return ResponseEntity.badRequest().body("No description id provided")
        return if (eventService.updateDescription(eventId, data["description"]!! as String))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }


    //TODO event not needed here
    @PutMapping(value = ["event/{event-id}/location-status"])
    fun updateLocationStatus(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ) : ResponseEntity<String> {
        if (!data.containsKey("location_id")) return ResponseEntity.badRequest().body("No description id provided")
        return if (locationService.updateLocation(eventId, data["location_id"]!! as Int))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

    @PostMapping(value = ["event"])
    fun createEvent(
        @RequestBody data: CreateEventData
    ) : Int? {
        return eventService.createEvent(data)
    }

//    Map
    @GetMapping(value = ["events/{user-id}/map/my-events"])
    fun getMapEvents(
        @PathVariable("user-id") userId: Int,
        @RequestParam limit: Int?
    ) : List<MapEventObj>? {
        return eventService.getMapLocations(userId, limit ?: 5)
    }

}