package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.repositories.InviteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InviteService {

    @Autowired
    lateinit var inviteRepository: InviteRepository

}