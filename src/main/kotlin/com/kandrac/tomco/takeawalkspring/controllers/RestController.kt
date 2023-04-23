package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.payloadEntities.CreateEventData
import com.kandrac.tomco.takeawalkspring.payloadEntities.FilterData
import com.kandrac.tomco.takeawalkspring.payloadEntities.MessageData
import com.kandrac.tomco.takeawalkspring.payloadEntities.ProfileEditData
import com.kandrac.tomco.takeawalkspring.responseEntities.*
import com.kandrac.tomco.takeawalkspring.security.UserSecurity
import com.kandrac.tomco.takeawalkspring.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
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
    fun getUserInvitations(@PathVariable("user-id") userId: Int): ResponseEntity<List<EventObj>?> {
        // TODO pagination
        val invites = eventService.getAllUserInvites(userId) ?: return ResponseEntity.ok(emptyList())

        invites.forEach { invite ->
//            val peopleNum = inviteService.getEventPeople(invite.eventId)?.size
            val peopleNum = inviteService.countEventPeople(invite.eventId)
            invite.peopleGoing = peopleNum
        }

        return ResponseEntity.ok(invites)
    }

    @GetMapping(value = ["/events/{user-id}/my-events"])
    fun getUserEvents(@PathVariable("user-id") userId: Int): ResponseEntity<List<EventObj>?> {
        // TODO pagination
        val events = eventService.getAllUserEvents(userId) ?: return ResponseEntity.ok(emptyList())

        events.forEach { event ->
//            val peopleNum = inviteService.getEventPeople(event.eventId)?.size
            val peopleNum = inviteService.countEventPeople(event.eventId)
            event.peopleGoing = peopleNum
        }

        return ResponseEntity.ok(events)
    }

    //    Accept & Decline

    @PutMapping(value = ["/events/{event-id}/accept"])
    fun acceptInvite(
        @PathVariable("event-id") eventId: Int,
        @RequestParam("user-id") userId: Int
    ): ResponseEntity<Int> {
        println("EventId: ${eventId} UserId: ${userId}")
        val inviteId = inviteService.acceptUserInvite(userId = userId.toInt(), eventId = eventId)
        return ResponseEntity.ok(inviteId)
    }

    @PutMapping(value = ["/events/{event-id}/decline"])
    fun declineInvite(
        @PathVariable("event-id") eventId: Int,
        @RequestParam("user-id") userId: Int
    ): ResponseEntity<Int> {
        val inviteId = inviteService.declineUserInvite(userId, eventId)
        return ResponseEntity.ok(inviteId)
    }


//    Filter

    @GetMapping(value = ["/events/{user-id}/my-events/filter"])
    fun filterEvents(
        @PathVariable("user-id") userId: Int,
        @RequestBody filter: FilterData
    ): ResponseEntity<List<EventObj>> {
        val events = eventService.filterEvents(userId, filter) ?: return ResponseEntity.ok(emptyList())

        return ResponseEntity.ok(events)
    }

    @GetMapping(value = ["/events/{user-id}/invitations/filter"])
    fun filterInvitations(
        @PathVariable("user-id") userId: Int,
        @RequestBody filter: FilterData
    ): ResponseEntity<List<EventObj>> {
        val events = eventService.filterInvitations(userId, filter) ?: return ResponseEntity.ok(emptyList())

        return ResponseEntity.ok(events)
    }


    //    Event Detail
    @GetMapping(value = ["/event/{event-id}/host"])
    fun getEventOwner(@PathVariable("event-id") eventId: Int): ResponseEntity<String?> {
        val owner = eventService.getEventOwner(eventId) ?: return ResponseEntity.ok("")
//        val ownerName = owner.username
        return ResponseEntity.ok(owner.username)
    }

    @GetMapping(value = ["/event/{event-id}/people"])
    fun getEventPeople(
        @PathVariable("event-id") eventId: Int,
        @RequestParam("include-pending") includePending: Boolean
    ): ResponseEntity<List<EventPeople>?> {
//        val eventPeople = inviteService.getEventPeople(eventId) ?: emptyList()
        val eventPeople = inviteService.getAllEventPeople(eventId, includePending) ?: emptyList()
        return ResponseEntity.ok(eventPeople)
    }

    @GetMapping(value = ["/event/{event-id}/description"])
    fun getEventDescription(@PathVariable("event-id") eventId: Int): ResponseEntity<String?> {
        val description = eventService.getEventDescription(eventId) ?: ""
        return ResponseEntity.ok(description)
    }

    @GetMapping(value = ["/event/{event-id}/status"])
    fun getEventStatus(@PathVariable("event-id") eventId: Int, auth: Authentication): ResponseEntity<String?> {
        val userId = (auth.principal as UserSecurity).id
        val inviteStatus = inviteService.getInviteStatus(userId.toInt(), eventId) ?: ""
        return ResponseEntity.ok(inviteStatus)
    }

    @GetMapping(value = ["/event/{event-id}/data"])
    fun getEventData(
        @PathVariable("event-id") eventId: Int,
        @RequestParam("user-id") userId: Int
    ): ResponseEntity<EventData> {
//        val userId = (auth.principal as UserSecurity).id

        val eventHost = eventService.getEventOwner(eventId)
        val eventPeople = inviteService.getEventPeople(eventId)
        val eventLocations = locationService.getEventLocations(eventId)
        val eventTime = eventService.getEventTimeDetail(eventId)
//        val eventStatus = inviteService.getInviteStatus(userId.toInt(), eventId)
        val eventStatus = inviteService.getInviteStatus(userId, eventId)

        val eventData = EventData(
            ownerId = eventHost!!.id!!,
            eventHost = eventHost.username!!,
//            eventPeople = eventPeople,
            eventLocations = eventLocations,
            eventTime = eventTime,
            eventStatus = eventStatus
        )

        return ResponseEntity.ok(eventData)
    }


    //    Profile
    @GetMapping(value = ["/user/{user-id}/profile"])
    fun getUserProfile(@PathVariable("user-id") userId: Int): ResponseEntity<ProfileObj?> {
        val userProfile = userService.getUserProfile(userId)
        return ResponseEntity.ok(userProfile)
    }

    //    Profile edit
    @PutMapping(value = ["/user/{user-id}/edit"])
    fun editUserProfile(
        @PathVariable("user-id") userId: Int,
        @RequestBody data: ProfileEditData
    ): ResponseEntity<String> {
        return if (userService.updateUserProfile(userId, data))
            ResponseEntity.status(HttpStatus.OK).body("Success") else ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("User not found")
    }

    @PutMapping(value = ["/user/{user-id}/profile-picture"])
    fun editUserProfile(@PathVariable("user-id") userId: Int, file: MultipartFile): ResponseEntity<String> {
        return if (userService.updateUserProfileImage(userId, file.bytes))
            ResponseEntity.ok("Success") else
            ResponseEntity.badRequest().body("User not found")
    }

    @DeleteMapping(value = ["/user/{user-id}/device-token"])
    fun deleteDeviceToken(@PathVariable("user-id") userId: Int): ResponseEntity<String> {
        userService.deleteDeviceToken(userId).also { return ResponseEntity.ok("Success") }
    }

    //    Search
    @GetMapping(value = ["/user/search"])
    fun searchForUser(@RequestParam("username") username: String): ResponseEntity<List<ProfileObj>> {
        val profiles = userService.searchForUser(username);
        return ResponseEntity.ok(profiles)
    }

    //    Chat
    @GetMapping(value = ["/chat/{event-id}/messages"])
    fun getEventMessages(@PathVariable("event-id") eventId: Int): ResponseEntity<List<MessageObj>?> {
        // TODO add pagination
        val eventMessages = messageService.getEventMessages(eventId)
        return ResponseEntity.ok(eventMessages)
    }

    @PostMapping(value = ["chat/{event-id}/message"])
    fun postEventMessage(
        @PathVariable("event-id") eventId: Int,
        @RequestBody message: MessageData
    ): ResponseEntity<String> {
        return if (messageService.addEventMessage(eventId, message))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

    @GetMapping(value = ["chat/{event-id}/device-tokens"])
    fun getDeviceTokens(@PathVariable("event-id") eventId: Int): ResponseEntity<List<String?>> {
        val deviceTokens = messageService.getDeviceTokens(eventId)
        return ResponseEntity.ok(deviceTokens)
    }

    //    Event Progress
    @GetMapping(value = ["event/{event-id}/pictures"])
    fun getEventPictures(@PathVariable("event-id") eventId: Int): ResponseEntity<List<String?>?> {
        val eventPictures = pictureService.getEventPictures(eventId)
        return ResponseEntity.ok(eventPictures)
    }

    @PostMapping(value = ["event/{event-id}/picture"])
    fun postEventPicture(
        @PathVariable("event-id") eventId: Int,
        file: MultipartFile
    ): ResponseEntity<String> {
        return if (pictureService.postEventPicture(eventId, file))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }


    //TODO consider deleting by id, no event id needed
    @DeleteMapping(value = ["event/{event-id}/picture"])
    fun deleteEventPicture(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ): ResponseEntity<String> {
        if (!data.containsKey("picture_id")) return ResponseEntity.badRequest().body("No picture id provided")
        return if (pictureService.deleteEventImage(eventId, data["picture_id"]!! as Int))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

    @DeleteMapping(value = ["event/{event-id}/picture/hard"])
    fun hardDeleteEventPicture(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ): ResponseEntity<String> {
        if (!data.containsKey("picture_id")) return ResponseEntity.badRequest().body("No picture id provided")
        return if (pictureService.hardDeleteEventImage(eventId, data["picture_id"]!! as Int))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

    @PutMapping(value = ["event/{event-id}/description"])
    fun updateEventDescription(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ): ResponseEntity<String> {
        if (!data.containsKey("description")) return ResponseEntity.badRequest().body("No description id provided")
        return if (eventService.updateDescription(eventId, data["description"]!! as String))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }


    //TODO event not needed here
    @PutMapping(value = ["event/{event-id}/location-status"])
    fun updateLocationStatus(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ): ResponseEntity<String> {
        if (!data.containsKey("location_id")) return ResponseEntity.badRequest().body("No description id provided")
        return if (locationService.updateLocation(eventId, data["location_id"]!! as Int))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

    @PostMapping(value = ["event"])
    fun createEvent(
        @RequestBody data: CreateEventData
    ): ResponseEntity<Int?> {
        val eventId = eventService.createEvent(data)
//        return ResponseEntity.ok(eventId)
        return ResponseEntity.status(HttpStatus.CREATED).body(eventId)
    }

    //    Map
    @GetMapping(value = ["events/{user-id}/map/my-events"])
    fun getMapEvents(
        @PathVariable("user-id") userId: Int,
        @RequestParam limit: Int?
    ): ResponseEntity<List<MapEventObj>?> {
        val mapEvents = eventService.getMapLocations(userId, limit ?: 5)
        return ResponseEntity.ok(mapEvents)
    }

    //    TOKEN
    @PostMapping(value = ["user/{user-id}/device-token"])
    fun setToken(
        @PathVariable("user-id") userId: Int,
        @RequestBody data: Map<String, Any>
    ): ResponseEntity<String> {
        if (!data.containsKey("deviceToken")) return ResponseEntity.badRequest().body("No token provided")
        return if (userService.setUserDeviceToken(userId, data["deviceToken"]!! as String))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }

}