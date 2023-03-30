package com.kandrac.tomco.takeawalkspring.security

import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import com.kandrac.tomco.takeawalkspring.services.RefreshTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
) {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var refreshTokenService: RefreshTokenService

    private val jwtToken = JwtTokenUtil()

    private fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.userDetailsService(userDetailsService)
        return authenticationManagerBuilder.build()
    }

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManager = authManager(http)

        http
            .authorizeHttpRequests().requestMatchers("/auth/***").permitAll()
            .anyRequest().authenticated().and()
            .csrf().disable()
            .authenticationManager(authenticationManager)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .addFilter(jwtAuthenticationFilter(authenticationManager, userRepository, refreshTokenService))
            .addFilter(JwtAuthorizationFilter(jwtToken, userDetailsService, authenticationManager))

        return http.build()
    }


    fun jwtAuthenticationFilter(
        authManager: AuthenticationManager,
        userRepository: UserRepository,
        refreshTokenService: RefreshTokenService
    ): JwtAuthenticationFilter {
        val jwtAuthenticationFilter =
            JwtAuthenticationFilter(jwtToken, authManager, userRepository, refreshTokenService)
        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login")
        return jwtAuthenticationFilter
    }

    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

}