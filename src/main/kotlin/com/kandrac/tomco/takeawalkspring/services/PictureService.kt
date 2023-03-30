package com.kandrac.tomco.takeawalkspring.services

import com.google.cloud.storage.Storage
import com.google.firebase.cloud.StorageClient
import com.kandrac.tomco.takeawalkspring.entities.Picture
import com.kandrac.tomco.takeawalkspring.repositories.EventRepository
import com.kandrac.tomco.takeawalkspring.repositories.PictureRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class PictureService {

    @Autowired
    lateinit var pictureRepository: PictureRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    fun getEventPictures(eventId: Int): List<String?>? {
        val event = eventRepository.findEventById(eventId) ?: return null
        val list = mutableListOf<String?>()
        val pictures = pictureRepository.findPicturesByEvent(event) ?: return null
        pictures.forEach {
            if (!it.deleted)
                list.add(it.link)
        }
        return list.toList()
    }

    fun postEventPicture(eventId: Int, file: MultipartFile): Boolean {
        val event = eventRepository.findEventById(eventId) ?: return false
        val newPicture = Picture(
            event = event,
            link = "test"
        )
        val bucket = StorageClient.getInstance().bucket()
        val objectName = "${event.name}1"
        bucket.create(objectName, file.inputStream, file.contentType)
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