package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.User
import com.kandrac.tomco.takeawalkspring.payloadEntities.ProfileEditData
import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.ProfileObj
import com.kandrac.tomco.takeawalkspring.Dto.RegisterDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder


    fun saveUser(credentials: RegisterDto): Int {
        val newUser = User(
            username = credentials.username,
            email = credentials.email,
            password = passwordEncoder.encode(credentials.password),
            events = null,
            invites = null,
            messages = null
        )

        val dbUser = userRepository.save(newUser)
        return dbUser.id!!
    }

    fun getUserProfile(userId: Int): ProfileObj? {
        val user: User = userRepository.findUserById(userId) ?: return null
        return ProfileObj(
            userName = user.username!!,
            email = user.email!!,
            bio = user.bio,
            profilePicture = user.picture
        )
    }

    fun updateUserProfile(userId: Int, data: ProfileEditData) : Boolean {
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

    fun getUserByEmail(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

}