package org.heatpump.backbone.services

import org.heatpump.backbone.config.JwtTokenProvider
import org.heatpump.backbone.models.HeatPumpModel
import org.heatpump.backbone.models.User
import org.heatpump.backbone.repositories.HeatPumpRepository
import org.heatpump.backbone.repositories.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val heatPumpRepository: HeatPumpRepository,
) {

    fun register(user: User): Map<String, String> {
        if (userRepository.findByUsername(user.username).isPresent) {
            throw Exception("User with username ${user.username} already exists")
        }

        val encryptedPassword = passwordEncoder.encode(user.password)

        val savedUser = userRepository.save(User(user.id, user.username, encryptedPassword))

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(savedUser.username, user.password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        val accessToken = jwtTokenProvider.generateAccessToken(savedUser.username)
        val refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.username)

        createDemoHeatPump(savedUser)

        return mapOf("accessToken" to accessToken, "refreshToken" to refreshToken)
    }

    private fun createDemoHeatPump(user: User) {

        val demoHeatPump = HeatPumpModel(name = "Demo Heat Pump", user = user)

        heatPumpRepository.save(demoHeatPump)
    }

    fun login(username: String, password: String): Map<String, String> {

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )

        SecurityContextHolder.getContext().authentication = authentication

        val user = userRepository.findByUsername(username)
            .orElseThrow { Exception("User not found") }

        val accessToken = jwtTokenProvider.generateAccessToken(user.username)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.username)

        return mapOf("accessToken" to accessToken, "refreshToken" to refreshToken)
    }

    fun refreshTokens(refreshToken: String): Map<String, String> {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw Exception("Invalid or expired refresh token")
        }

        val username = jwtTokenProvider.getUsernameFromToken(refreshToken)

        val newAccessToken = jwtTokenProvider.generateAccessToken(username)
        val newRefreshToken = jwtTokenProvider.generateRefreshToken(username)

        return mapOf("accessToken" to newAccessToken, "refreshToken" to newRefreshToken)
    }

    fun logout() {
        SecurityContextHolder.clearContext()
    }

    fun deleteUser(username: String): String {

        val user = userRepository.findByUsername(username)
            .orElseThrow { Exception("User with username $username not found") }

        userRepository.delete(user)

        return "User with username $username has been successfully deleted"
    }
}