package com.kandrac.tomco.takeawalkspring.services

import com.kandrac.tomco.takeawalkspring.entities.RefreshToken
import com.kandrac.tomco.takeawalkspring.entities.User
import com.kandrac.tomco.takeawalkspring.repositories.RefreshTokenRepository
import com.kandrac.tomco.takeawalkspring.Dto.JwtRefreshRequestDto
import com.kandrac.tomco.takeawalkspring.Dto.JwtResponseDto
import com.kandrac.tomco.takeawalkspring.security.JwtTokenUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Service
class RefreshTokenService {

    @Autowired
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    lateinit var jwtUtils: JwtTokenUtil

    fun createToken(user: User): String {
        val refreshToken = RefreshToken(
            token = UUID.randomUUID().toString(),
            user = user,
            expiration = ZonedDateTime.now().plusMonths(6)
        )

        val dbUserRefreshToken = refreshTokenRepository.findRefreshTokenByUser(user)
        if (dbUserRefreshToken != null) refreshTokenRepository.delete(dbUserRefreshToken)

        refreshTokenRepository.save(refreshToken)

        return refreshToken.token!!
    }

    fun getRefreshToken(refreshRequestDto: JwtRefreshRequestDto): JwtResponseDto {
        val token: RefreshToken? = refreshTokenRepository.findRefreshTokenByToken(refreshRequestDto.refreshToken)

        if (token == null) throw ClassNotFoundException("Refresh token not found")

        if (isTokenExpired(token.expiration!!)) {
            refreshTokenRepository.delete(token)
            throw java.lang.RuntimeException("Refresh token expired")
        }

        val jwtToken = jwtUtils.generateToken(token.user!!.email!!, token.user!!.id.toString()).also { updateToken(token) }

        return JwtResponseDto(jwtToken, token.token!!)
    }


    private fun isTokenExpired(expiration: ZonedDateTime): Boolean {
        return expiration.isBefore(ZonedDateTime.now(ZoneId.systemDefault()))
    }

    private fun updateToken(refreshToken: RefreshToken) {
        refreshToken.expiration = (ZonedDateTime.now(ZoneId.systemDefault()))
        refreshTokenRepository.save(refreshToken)
    }

}