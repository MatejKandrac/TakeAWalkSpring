package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.Event
import com.kandrac.tomco.takeawalkspring.repositories.EventRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.EventObj
import com.kandrac.tomco.takeawalkspring.responseEntities.EventTimeDetailObj
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EventService {

    @Autowired
    lateinit var eventRepository: EventRepository

    fun getEventOwner(eventId: Int): String? {
        val event: Event = eventRepository.findEventById(eventId) ?: return null
        return event.user!!.username!!
    }

    fun getEventDescription(eventId: Int): String? {
        val event: Event = eventRepository.findEventById(eventId) ?: return null
        return event.description
    }

    fun getAllUserEvents(userId: Int): List<EventObj>? {
        val events = eventRepository.getAllUserEvents(userId) ?: return null
        val resultEvents = mutableListOf<EventObj>()


        for (event in events) {
            resultEvents.add(
                EventObj(
                    name = event!!.name!!,
                    owner = event!!.user!!.username!!,
                    start = event!!.start!!,
                    end = event.endDate,
                    peopleGoing = null,
                    places = event.locations!!.size,
                    eventId = event!!.id!!
                )
            )
        }

        return resultEvents
    }

    fun getAllUserInvites(userId: Int): List<EventObj>? {
        val events: List<Event> = eventRepository.getAllUserInvites(userId) ?: return null
        val resultInvites = mutableListOf<EventObj>()

        for (event in events) {
            resultInvites.add(
                EventObj(
                    name = event.name!!,
                    owner = event.user!!.username!!,
                    start = event.start!!,
                    end = event.endDate,
                    peopleGoing = null,
                    places = event.locations!!.size,
                    eventId = event!!.id!!
                )
            )
        }

        return resultInvites
    }

    fun getEventTimeDetail(eventId: Int): EventTimeDetailObj {
        val event = eventRepository.findEventById(eventId)

        return EventTimeDetailObj(
            start = event!!.start,
            end = event!!.endDate
        )
    }

}