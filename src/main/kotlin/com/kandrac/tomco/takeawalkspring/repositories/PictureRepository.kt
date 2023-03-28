package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.Picture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PictureRepository : JpaRepository<Picture, Long> {
}