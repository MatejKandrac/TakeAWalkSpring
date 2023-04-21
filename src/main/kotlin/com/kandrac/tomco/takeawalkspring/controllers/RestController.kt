package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.payloadEntities.CreateEventData
import com.kandrac.tomco.takeawalkspring.payloadEntities.MessageData
import com.kandrac.tomco.takeawalkspring.payloadEntities.ProfileEditData
import com.kandrac.tomco.takeawalkspring.payloadEntities.WeatherData
import com.kandrac.tomco.takeawalkspring.responseEntities.*
import com.kandrac.tomco.takeawalkspring.security.UserSecurity
import com.kandrac.tomco.takeawalkspring.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

    @Autowired
    lateinit var weatherService: WeatherService
//    Invitations and MyEvents

    @GetMapping(value = ["/events/{user-id}/invitations"])
    fun getUserInvitations(@PathVariable("user-id") userId: Int): ResponseEntity<List<EventObj>?> {
        // TODO pagination
        val invites = eventService.getAllUserInvites(userId) ?: return ResponseEntity.ok(emptyList())

        invites.forEach { invite ->
            val peopleNum = inviteService.getEventPeople(invite.eventId)?.size
            invite.peopleGoing = peopleNum
        }

        return ResponseEntity.ok(invites)
    }

    @GetMapping(value = ["/events/{user-id}/my-events"])
    fun getUserEvents(@PathVariable("user-id") userId: Int): ResponseEntity<List<EventObj>?> {
        // TODO pagination
        val events = eventService.getAllUserEvents(userId) ?: return ResponseEntity.ok(emptyList())

        events.forEach { event ->
            val peopleNum = inviteService.getEventPeople(event.eventId)?.size
            event.peopleGoing = peopleNum
        }

        return ResponseEntity.ok(events)
    }

    //    Accept & Decline

    @PutMapping(value = ["/events/{user-id}/accept"])
    fun acceptInvite(@PathVariable("user-id") userId: Int, @RequestParam("event-id") eventId: Int): ResponseEntity<Int> {
        val inviteId = inviteService.acceptUserInvite(userId, eventId)
        return ResponseEntity.ok(inviteId)
    }

    @PutMapping(value = ["/events/{user-id}/decline"])
    fun declineInvite(@PathVariable("user-id") userId: Int, @RequestParam("event-id") eventId: Int): ResponseEntity<Int> {
        val inviteId = inviteService.declineUserInvite(userId, eventId)
        return ResponseEntity.ok(inviteId)
    }

    //    Event Detail
    @GetMapping(value = ["/event/{event-id}/host"])
    fun getEventOwner(@PathVariable("event-id") eventId: Int): ResponseEntity<String?> {
        val ownerName = eventService.getEventOwner(eventId) ?: ""
        return ResponseEntity.ok(ownerName)
    }

    @GetMapping(value = ["/event/{event-id}/people"])
    fun getEventPeople(@PathVariable("event-id") eventId: Int): ResponseEntity<List<String>?> {
        val eventPeople = inviteService.getEventPeople(eventId) ?: emptyList()
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
    fun getEventData(@PathVariable("event-id") eventId: Int, auth: Authentication): ResponseEntity<EventData> {
        val userId = (auth.principal as UserSecurity).id

        val eventHost = eventService.getEventOwner(eventId)
        val eventPeople = inviteService.getEventPeople(eventId)
        val eventLocations = locationService.getEventLocations(eventId)
        val eventTime = eventService.getEventTimeDetail(eventId)
        val eventStatus = inviteService.getInviteStatus(userId.toInt(), eventId)

        val eventData = EventData(
            eventHost = eventHost!!,
            eventPeople = eventPeople,
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
    fun editUserPicture(
            @PathVariable("user-id") userId: Int,
            @RequestBody file: MultipartFile): ResponseEntity<String> {
        return if (userService.updateUserProfileImage(userId, file))
            ResponseEntity.ok("Success") else
            ResponseEntity.badRequest().body("User not found")
    }

    //    Search
    @GetMapping(value = ["/user/{user_id}/search"])
    fun searchForUser(
            @PathVariable("user_id") userId: Int,
            @RequestParam("username") username: String): ResponseEntity<List<SearchPersonObj>> {
        val profiles = userService.searchForUser(userId, username)
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

    //    Event Progress
    @GetMapping(value = ["event/{event-id}/pictures"])
    fun getEventPictures(@PathVariable("event-id") eventId: Int): ResponseEntity<List<String?>?> {
        val eventPictures = pictureService.getEventPictures(eventId)
        return ResponseEntity.ok(eventPictures)
    }

    @PostMapping(value = ["event/{event-id}/picture"])
    fun postEventPicture(
        @PathVariable("event-id") eventId: Int,
        @RequestBody file: MultipartFile
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

    @PutMapping(value = ["event/{event-id}/description"])
    fun updateEventDescription(
        @PathVariable("event-id") eventId: Int,
        @RequestBody data: Map<String, Any>
    ): ResponseEntity<String> {
        if (!data.containsKey("description")) return ResponseEntity.badRequest().body("No description id provided")
        return if (eventService.updateDescription(eventId, data["description"]!! as String))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }


    @PutMapping(value = ["event/{event-id}/next-location"])
    fun updateLocationStatus(
        @PathVariable("event-id") eventId: Int
    ): ResponseEntity<String> {
        return eventService.setNextLocation(eventId)
    }

    @PostMapping(value = ["event"])
    fun createEvent(
        @RequestBody data: CreateEventData
    ): ResponseEntity<Int?> {
        val eventId = eventService.createEvent(data)
//        return ResponseEntity.ok(eventId)
        return ResponseEntity.status(HttpStatus.CREATED).body(eventId)
    }

    @DeleteMapping(value = ["event/{event-id}"])
    fun deleteEvent(
            @PathVariable("event-id") eventId: Int
    ): ResponseEntity<Int?> {
        val deleteId = eventService.deleteEvent(eventId)
        return ResponseEntity.ok(deleteId)
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

    @GetMapping(value = ["picture/{picture_link}"])
    fun getPicture(
            @PathVariable("picture_link") link: String
    ) : ResponseEntity<ByteArray> {
        val mediaType = if(link.endsWith(".jpg") || link.endsWith(".jpeg"))
            MediaType.IMAGE_JPEG else MediaType.IMAGE_PNG
        return ResponseEntity.ok().contentType(mediaType).body(pictureService.getPicture(link))
    }

    @GetMapping(value = ["weather"])
    fun getWeather(
            @RequestBody() data: WeatherData
    ) : ResponseEntity<List<WeatherObj>>? {
        return weatherService.getWeatherData(data.lat, data.lon, data.date)
    }

}