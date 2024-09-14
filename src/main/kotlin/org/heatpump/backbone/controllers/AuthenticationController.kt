package org.heatpump.backbone.controllers

import org.heatpump.backbone.config.SecurityUtils
import org.heatpump.backbone.models.User
import org.heatpump.backbone.services.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
    private val securityUtils: SecurityUtils
) {

    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<Map<String, String>> {
        return try {
            println("User registering: ${user.username} ${user.password}")

            // Call the register method to return both access and refresh tokens
            val tokens = authenticationService.register(user)
            val response = mapOf("username" to user.username) + tokens

            println("User registered: ${user.username}")
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Registration failed: ${e.message}"))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        return try {
            // Call the login method to return both access and refresh tokens
            val tokens = authenticationService.login(loginRequest.username, loginRequest.password)
            val response = mapOf("username" to loginRequest.username) + tokens
            ResponseEntity.ok(response)
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid username or password"))
        } catch (e: UsernameNotFoundException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "User not found"))
        }
    }

    @PostMapping("/refresh-token")
    fun refreshTokens(@RequestBody tokenRequest: TokenRefreshRequest): ResponseEntity<Map<String, String>> {
        return try {
            val tokens = authenticationService.refreshTokens(tokenRequest.refreshToken)
            ResponseEntity.ok(tokens)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid or expired refresh token"))
        }
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<Map<String, String?>> {
        val currentUsername = securityUtils.getCurrentUsername()
        return if (currentUsername != null) {
            ResponseEntity.ok(mapOf("username" to currentUsername))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Unauthorized"))
        }
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        authenticationService.logout()
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }

    @DeleteMapping("/delete/{username}")
    fun deleteUser(@PathVariable username: String): ResponseEntity<Map<String, String>> {
        return try {
            val message = authenticationService.deleteUser(username)
            ResponseEntity.ok(mapOf("message" to message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "User not found: ${e.message}"))
        }
    }
}

data class LoginRequest(val username: String, val password: String)
data class TokenRefreshRequest(val refreshToken: String)
