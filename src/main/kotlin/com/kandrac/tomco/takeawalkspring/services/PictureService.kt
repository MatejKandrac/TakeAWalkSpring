package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.Picture
import com.kandrac.tomco.takeawalkspring.repositories.EventRepository
import com.kandrac.tomco.takeawalkspring.repositories.PictureRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PictureService {

    @Autowired
    lateinit var pictureRepository: PictureRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    fun getEventPictures(eventId: Int): List<Picture>? {
        val event = eventRepository.findEventById(eventId) ?: return null
        return pictureRepository.findPicturesByEvent(event)
    }

    fun postEventPicture(eventId: Int, file: ByteArray): Boolean {
        val event = eventRepository.findEventById(eventId) ?: return false
        val newPicture = Picture(
            event = event,
            link = "test"
        )
        //TODO send to firebase storage
        pictureRepository.save(newPicture)
        return true
    }

    fun deleteEventImage(eventId: Int, pictureId: Int): Boolean {
        val event = eventRepository.findEventById(eventId) ?: return false
        val picture = pictureRepository.findPictureById(pictureId) ?: return false
        picture.deleted = true
        pictureRepository.save(picture)
        return true
    }
}