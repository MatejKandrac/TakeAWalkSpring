package com.kandrac.tomco.takeawalkspring.security

import com.kandrac.tomco.takeawalkspring.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserDetailService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findUserByEmail(username) ?: throw UsernameNotFoundException("$username not found")
        return UserSecurity(
            user.id.toString(),
            user.email.toString(),
            user.password.toString(),
            Collections.singleton(SimpleGrantedAuthority("user"))
        )
    }

}