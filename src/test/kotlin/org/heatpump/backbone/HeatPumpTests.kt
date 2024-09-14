package org.heatpump.backbone

import org.heatpump.backbone.config.SecurityUtils
import org.heatpump.backbone.models.HeatPumpModel
import org.heatpump.backbone.models.MetricsModel
import org.heatpump.backbone.models.User
import org.heatpump.backbone.repositories.HeatPumpRepository
import org.heatpump.backbone.repositories.MetricsRepository
import org.heatpump.backbone.repositories.UserRepository
import org.heatpump.backbone.services.HeatPumpService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
class HeatPumpServiceTest {

    private val heatPumpRepository: HeatPumpRepository = mock(HeatPumpRepository::class.java)
    private val metricsRepository: MetricsRepository = mock(MetricsRepository::class.java)
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val securityUtils: SecurityUtils = mock(SecurityUtils::class.java)

    private val heatPumpService = HeatPumpService(userRepository, heatPumpRepository, metricsRepository, securityUtils)

    @Test
    fun `should return all heat pumps with latest metrics for authorized user`() {

        `when`(securityUtils.getCurrentUsername()).thenReturn("test_user")

        val user = User(id = 1, username = "test_user", password = "password")
        `when`(userRepository.findByUsername("test")).thenReturn(Optional.of(user))

        val heatPump = HeatPumpModel(id = 1L, name = "Heat Pump DEMO", user = user)
        `when`(heatPumpRepository.findByUser(user)).thenReturn(listOf(heatPump))

        val metrics = MetricsModel(
            id = 1L,
            temperature = 22.5,
            efficiency = 90.0,
            timestamp = LocalDateTime.now(),
            heatPump = heatPump
        )
        `when`(metricsRepository.findTopByHeatPumpOrderByTimestampDesc(heatPump)).thenReturn(metrics)

        val result = heatPumpService.getAllHeatPumpsWithLatestMetrics("test_user")

        assertEquals(1, result.size)
        assertEquals("Heat Pump DEMO", result[0].heatPumpName)
        assertNotNull(result[0].latestMetrics)
        assertEquals(22.5, result[0].latestMetrics?.temperature)
    }

    @Test
    fun `should throw IllegalAccessException for unauthorized access`() {
        `when`(securityUtils.getCurrentUsername()).thenReturn("test_user2")

        val user = User(id = 1, username = "test_user", password = "password")
        `when`(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user))

        assertThrows<IllegalAccessException> {
            heatPumpService.getAllHeatPumpsWithLatestMetrics("test_user")
        }
    }

    @Test
    fun `should return empty metrics if no metrics available`() {
        `when`(securityUtils.getCurrentUsername()).thenReturn("test_user")

        val user = User(id = 1, username = "test_user", password = "password")
        `when`(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user))

        val heatPump = HeatPumpModel(id = 1L, name = "Heat Pump DEMO", user = user)
        `when`(heatPumpRepository.findByUser(user)).thenReturn(listOf(heatPump))

        `when`(metricsRepository.findTopByHeatPumpOrderByTimestampDesc(heatPump)).thenReturn(null)

        val result = heatPumpService.getAllHeatPumpsWithLatestMetrics("password")

        assertEquals(1, result.size)
        assertEquals("Heat Pump 1", result[0].heatPumpName)
        assertNull(result[0].latestMetrics)  // No metrics available
    }
}