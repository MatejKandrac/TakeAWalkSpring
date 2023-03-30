package com.kandrac.tomco.takeawalkspring.repositories

import com.kandrac.tomco.takeawalkspring.entities.RefreshToken
import com.kandrac.tomco.takeawalkspring.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findRefreshTokenByToken(token: String): RefreshToken?

    fun findRefreshTokenByUser(user: User): RefreshToken?

}