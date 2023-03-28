package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.Event
import com.kandrac.tomco.takeawalkspring.entities.Invite
import com.kandrac.tomco.takeawalkspring.entities.Location
import com.kandrac.tomco.takeawalkspring.entities.Status
import com.kandrac.tomco.takeawalkspring.payloadEntities.CreateEventData
import com.kandrac.tomco.takeawalkspring.repositories.EventRepository
import com.kandrac.tomco.takeawalkspring.repositories.InviteRepository
import com.kandrac.tomco.takeawalkspring.repositories.LocationRepository
import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EventService {

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var locationRepository: LocationRepository

    @Autowired
    lateinit var inviteRepository: InviteRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun getEventOwner(eventId: Int): String? {
        val event: Event = eventRepository.findEventById(eventId) ?: return null
        return event.user!!.username
    }

    fun getEventDescription(eventId: Int): String? {
        val event: Event = eventRepository.findEventById(eventId) ?: return null
        return event.description
    }

    fun updateDescription(eventId: Int, desc: String): Boolean {
        val event: Event = eventRepository.findEventById(eventId) ?: return false
        event.description = desc
        return true
    }

    fun createEvent(data: CreateEventData): Int? {
        val user = userRepository.findUserById(data.ownerId) ?: return null
        var event = Event(
            user = user,
            endDate = data.endDate,
            start = data.start,
            description = data.description,
            name = data.name
        )
        event = eventRepository.save(event)

        data.locations.forEach {
            locationRepository.save(
                Location(
                    name = it.name,
                    latitude = it.lat,
                    longitude = it.lon,
                    visited = false,
                    locationOrder = it.order,
                    event = event
                )
            )
        }

        data.users.forEach {
            val inviteUser = userRepository.findUserById(it)
            inviteRepository.save(Invite(
                event = event,
                user = inviteUser,
                status = Status.PENDING
            ))
        }

        return event.id
    }

}