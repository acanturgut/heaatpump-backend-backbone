package org.heatpump.backbone.services

import org.heatpump.backbone.config.SecurityUtils
import org.heatpump.backbone.models.*
import org.heatpump.backbone.repositories.HeatPumpRepository
import org.heatpump.backbone.repositories.MetricsRepository
import org.heatpump.backbone.repositories.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class HeatPumpService(
    private val userRepository: UserRepository,
    private val heatPumpRepository: HeatPumpRepository,
    private val metricsRepository: MetricsRepository,
    private val securityUtils: SecurityUtils
) {

    fun getAllHeatPumpsWithLatestMetrics(username: String): List<HeatPumpWithLatestMetricsDTO> {
        val currentUsername = securityUtils.getCurrentUsername()
        val user = userRepository.findByUsername(username).orElseThrow { Exception("User not found") }

        if (user.username != currentUsername) {
            throw IllegalAccessException("You are not authorized to access this resource")
        }

        val heatPumps = heatPumpRepository.findByUser(user)
        return heatPumps.map { heatPump ->
            val latestMetrics = metricsRepository.findTopByHeatPumpOrderByTimestampDesc(heatPump)
            HeatPumpWithLatestMetricsDTO(
                heatPumpName = heatPump.name,
                latestMetrics = latestMetrics?.toDTO()
            )
        }
    }

    fun getHeatPumpWithAllMetrics(heatPumpId: Long): HeatPumpWithAllMetricsDTO {
        val currentUsername = securityUtils.getCurrentUsername()
        val heatPump = heatPumpRepository.findById(heatPumpId).orElseThrow { Exception("Heat pump not found") }

        if (heatPump.user.username != currentUsername) {
            throw IllegalAccessException("You are not authorized to access this resource")
        }

        val allMetrics = metricsRepository.findAllByHeatPump(heatPump)

        return HeatPumpWithAllMetricsDTO(
            heatPumpName = heatPump.name,
            metrics = allMetrics.map { it.toDTO() }
        )
    }

    @Scheduled(fixedRate = 60000)
    fun updateMetricsForAllHeatPumps() {

        val heatPumps: List<HeatPumpModel> = heatPumpRepository.findAll()

        if (heatPumps.isEmpty()) {
            println("No heat pumps found, skipping update...")
            return
        }

        for (heatPump in heatPumps) {
            val temperature = Random.nextDouble(15.0, 30.0)
            val efficiency = Random.nextDouble(0.5, 1.0)

            val metrics = MetricsModel(
                temperature = temperature,
                efficiency = efficiency,
                heatPump = heatPump
            )

            metricsRepository.save(metrics)
            println("Updated metrics for HeatPump: ${heatPump.id} -> Temperature: $temperature, Efficiency: $efficiency")
        }
    }
}


