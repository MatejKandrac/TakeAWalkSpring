package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Event
import com.kandrac.tomco.takeawalkspring.entities.Picture
import org.springframework.data.domain.Example
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.FluentQuery
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.function.Function

@Repository
interface PictureRepository : JpaRepository<Picture, Long> {

    fun findPictureById(pictureId: Int) : Picture?
    fun findPicturesByEvent(event: Event) : List<Picture>?
}