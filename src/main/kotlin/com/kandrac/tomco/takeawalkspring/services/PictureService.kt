package com.kandrac.tomco.takeawalkspring.services

import com.google.firebase.cloud.StorageClient
import com.kandrac.tomco.takeawalkspring.entities.Picture
import com.kandrac.tomco.takeawalkspring.repositories.EventRepository
import com.kandrac.tomco.takeawalkspring.repositories.PictureRepository
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.channels.Channels

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
        val index = eventRepository.getLastPictureIndex(eventId)
        val extension = file.originalFilename?.split(".")?.last() ?: return false
        val objectName = "event_${event.id}_${index + 1}.${extension}"
        val newPicture = Picture(
            event = event,
            link = objectName
        )
        pictureRepository.save(newPicture)
        val bucket = StorageClient.getInstance().bucket()
        bucket.create(objectName, file.inputStream, file.contentType)
        return true
    }


    fun getPicture(pictureLink: String) : ByteArray {
        val bucket = StorageClient.getInstance().bucket()
        val blob = bucket.get(pictureLink)
        val channel = blob.reader()
        val stream = Channels.newInputStream(channel)
        return IOUtils.toByteArray(stream)
    }

    fun deleteEventImage(pictureId: Int): Boolean {
        val picture = pictureRepository.findPictureById(pictureId) ?: return false
        picture.deleted = true
        pictureRepository.save(picture)
        return true
    }

    fun hardDeleteEventImage(eventId: Int, pictureId: Int): Boolean {
        val picture = pictureRepository.findPictureById(pictureId) ?: return false
        pictureRepository.delete(picture)
        return true
    }
}