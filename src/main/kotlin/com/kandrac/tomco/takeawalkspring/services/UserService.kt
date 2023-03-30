package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.User
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

    fun getUserByEmail(email: String): User? {
        return userRepository.findUserByEmail(email)
    }

}