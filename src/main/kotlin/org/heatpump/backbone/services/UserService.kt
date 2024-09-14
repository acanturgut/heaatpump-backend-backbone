package org.heatpump.backbone.services

import org.heatpump.backbone.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val user = userRepository.findByUsername(username).orElseThrow{throw UsernameNotFoundException("User not found")}

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return org.springframework.security.core.userdetails.User(
            user.username, user.password, authorities,
        )
    }
}