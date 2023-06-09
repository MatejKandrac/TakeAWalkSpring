package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findUserById(userId: Int): User?

    fun findUserByEmail(email: String?): User?

    fun findUserByUsername(username: String) : User?

}