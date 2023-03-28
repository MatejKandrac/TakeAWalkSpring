package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.User
import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.ProfileObj
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun getUserProfile(userId: Int): ProfileObj? {
        val user: User = userRepository.findUserById(userId) ?: return null
        return ProfileObj(
            userName = user.username!!,
            email = user.email!!,
            bio = user.bio,
            profilePicture = user.picture
        )
    }

}