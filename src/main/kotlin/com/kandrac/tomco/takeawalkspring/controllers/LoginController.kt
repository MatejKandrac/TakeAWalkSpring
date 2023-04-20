package com.kandrac.tomco.takeawalkspring.controllers

import com.kandrac.tomco.takeawalkspring.Dto.JwtRefreshRequestDto
import com.kandrac.tomco.takeawalkspring.Dto.JwtResponseDto
import com.kandrac.tomco.takeawalkspring.Dto.RegisterDto
import com.kandrac.tomco.takeawalkspring.entities.User
import com.kandrac.tomco.takeawalkspring.security.JwtTokenUtil
import com.kandrac.tomco.takeawalkspring.services.RefreshTokenService
import com.kandrac.tomco.takeawalkspring.services.UserService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var refreshTokenService: RefreshTokenService


    @PostMapping(value = ["auth/register"])
    fun register(@RequestBody credentials: RegisterDto): Any? {
        val dbUser: User? = userService.getUserByEmail(credentials.email)

        if (dbUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with given email already exists")
        }

        val user = userService.saveUser(credentials)

        val jwtToken = jwtTokenUtil.generateToken(credentials.email, user.id.toString())
        val refreshToken = refreshTokenService.createToken(user)

        return JwtResponseDto(jwtToken, refreshToken)
    }

    @PostMapping(value = ["auth/refresh"])
    fun refreshToken(@RequestBody refreshRequestDto: JwtRefreshRequestDto): JwtResponseDto {
        return refreshTokenService.getRefreshToken(refreshRequestDto)
    }

}