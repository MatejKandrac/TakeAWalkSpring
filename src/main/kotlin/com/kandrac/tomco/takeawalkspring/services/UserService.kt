package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.User
import com.kandrac.tomco.takeawalkspring.payloadEntities.ProfileEdit
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

    fun updateUserProfile(userId: Int, data: ProfileEdit) : Boolean {
        val user: User = userRepository.findUserById(userId) ?: return false
        user.username = data.username ?: user.username
        user.password = data.password ?: user.password
        user.bio = data.bio ?: user.bio
        userRepository.save(user)
        return true
    }

    fun updateUserProfileImage(userId: Int, bytes: ByteArray): Boolean {
        print(bytes)
//        val user: User = userRepository.findUserById(userId) ?: return false
        // TODO upload to firebase storage
        return true
    }

}