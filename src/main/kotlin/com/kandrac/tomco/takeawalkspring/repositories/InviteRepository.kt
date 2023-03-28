package com.kandrac.tomco.takeawalkspring.repositories


import com.kandrac.tomco.takeawalkspring.entities.Invite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InviteRepository : JpaRepository<Invite, Long> {

}