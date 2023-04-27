package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.payloadEntities.CreateEventData
import com.kandrac.tomco.takeawalkspring.payloadEntities.FilterData
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
        @RequestParam("include-pending", defaultValue = "false") includePending: Boolean
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

        val event = eventService.getEvent(eventId)
        val eventLocations = locationService.getEventLocations(eventId)
        val eventTime = eventService.getEventTimeDetail(eventId)
        val eventStatus = inviteService.getInviteStatus(userId, eventId)

        val eventData = EventData(
            ownerId = event!!.user!!.id!!,
            eventName = event.name!!,
            eventHost = event.user!!.username!!,
            eventLocations = eventLocations,
            eventTime = eventTime,
            eventStatus = eventStatus,
            currentIndex = event.actualLocation
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

    @DeleteMapping(value = ["/user/{user-id}/device-token"])
    fun deleteDeviceToken(@PathVariable("user-id") userId: Int): ResponseEntity<String> {
        userService.deleteDeviceToken(userId).also { return ResponseEntity.ok("Success") }
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
    fun getEventMessages(
        @PathVariable("event-id") eventId: Int,
        @RequestParam("page") pageNumber: Int,
        @RequestParam("size") pageSize: Int
    ): ResponseEntity<List<MessageObj>> {
        val eventMessages = messageService.getEventMessages(eventId, pageNumber, pageSize) ?: return ResponseEntity.ok(emptyList())
        return ResponseEntity.ok(eventMessages)
    }

    @PostMapping(value = ["chat/{event-id}/message"])
    fun postEventMessage(
        @PathVariable("event-id") eventId: Int,
        @RequestBody message: MessageData
    ): ResponseEntity<Int> {
        val result = messageService.addEventMessage(eventId, message)
        return if (result != null)
            ResponseEntity.ok(result) else ResponseEntity.badRequest().body(-1)
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
        @RequestBody file: MultipartFile
    ): ResponseEntity<String> {
        return if (pictureService.postEventPicture(eventId, file))
            ResponseEntity.ok("Success") else ResponseEntity.badRequest().body("Invalid data")
    }


    //TODO consider deleting by id, no event id needed
    @DeleteMapping(value = ["picture/{picture-id}"])
    fun deleteEventPicture(
        @PathVariable("picture-id") pictureId: Int,
    ): ResponseEntity<String> {
        return if (pictureService.deleteEventImage(pictureId))
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
    ) : ResponseEntity<List<WeatherDateObj>>? {
        return weatherService.getWeatherData(data.lat, data.lon, data.dateStart, data.dateEnd)
    }

}