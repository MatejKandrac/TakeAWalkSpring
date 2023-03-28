package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.entities.Picture
import com.kandrac.tomco.takeawalkspring.payloadEntities.CreateEventData
import com.kandrac.tomco.takeawalkspring.payloadEntities.MessageData
import com.kandrac.tomco.takeawalkspring.payloadEntities.ProfileEditData
import com.kandrac.tomco.takeawalkspring.responseEntities.MessageObj
import com.kandrac.tomco.takeawalkspring.responseEntities.ProfileObj
import com.kandrac.tomco.takeawalkspring.services.*
import org.springframework.beans.factory.annotation.Autowired
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
    fun getEventPictures(@PathVariable("event-id") eventId: Int): List<Picture>? {
        return pictureService.getEventPictures(eventId)
    }

    @PostMapping(value = ["event/{event-id}/picture"])
    fun postEventPicture(
        @PathVariable("event-id") eventId: Int,
        file: MultipartFile
    ) : ResponseEntity<String> {
        return if (pictureService.postEventPicture(eventId, file.bytes))
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
}