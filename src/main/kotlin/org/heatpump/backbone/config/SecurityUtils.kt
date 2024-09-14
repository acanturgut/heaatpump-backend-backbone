package org.heatpump.backbone.config

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class SecurityUtils {
    fun getCurrentUsername(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        return when (val principal = authentication.principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> null
        }
    }
}