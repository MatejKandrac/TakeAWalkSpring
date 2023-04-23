package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.User
import com.kandrac.tomco.takeawalkspring.payloadEntities.ProfileEditData
import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import com.kandrac.tomco.takeawalkspring.responseEntities.ProfileObj
import com.kandrac.tomco.takeawalkspring.Dto.RegisterDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    private val SEARCH_CONDITIONS_MATCH_ANY = ExampleMatcher
            .matching()
            .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())

    fun saveUser(credentials: RegisterDto): User {
        val newUser = User(
            username = credentials.username,
            email = credentials.email,
            password = passwordEncoder.encode(credentials.password),
            events = null,
            invites = null,
            messages = null
        )

        val dbUser = userRepository.save(newUser)
        return dbUser
    }

    fun getUserProfile(userId: Int): ProfileObj? {
        val user: User = userRepository.findUserById(userId) ?: return null
        return ProfileObj(
            id = user.id!!,
            userName = user.username!!,
            email = user.email!!,
            bio = user.bio,
            profilePicture = user.picture
        )
    }

    fun updateUserProfile(userId: Int, data: ProfileEditData) : Boolean {
        val user: User = userRepository.findUserById(userId) ?: return false
        user.username = data.username ?: user.username
//        user.password =  data.password ?: user.password
        user.password =  if (data.password != null) passwordEncoder.encode(data.password) else user.password
        user.bio = data.bio ?: user.bio
        userRepository.save(user)
        return true
    }

    fun setUserDeviceToken(userId: Int, token: String):Boolean {
        val user = userRepository.findUserById(userId) ?: return false
        user.deviceToken = token
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

    fun searchForUser(username: String): List<ProfileObj> {
        val example: Example<User> = Example.of(User(username=username), SEARCH_CONDITIONS_MATCH_ANY)
        val list = userRepository.findAll(example)
        val response = mutableListOf<ProfileObj>()
        for (user in list) {
            response.add(ProfileObj(user.id!!, user.username!!, user.email!!, user.bio, user.picture))
        }
        return response
    }

    fun deleteDeviceToken(userId: Int): Int? {
        val user = userRepository.findUserById(userId) ?: return null
        user.deviceToken = null
        userRepository.save(user).also { return user.id }
    }

}