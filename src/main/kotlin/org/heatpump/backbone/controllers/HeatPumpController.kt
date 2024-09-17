package org.heatpump.backbone.controllers

import org.heatpump.backbone.models.HeatPumpWithAllMetricsDTO
import org.heatpump.backbone.models.HeatPumpWithLatestMetricsDTO
import org.heatpump.backbone.services.HeatPumpService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/heatpumps")
class HeatPumpController(private val heatPumpService: HeatPumpService) {

    @GetMapping("/{userName}")
    fun getAllHeatPumpsWithLatestMetrics(@PathVariable userName: String): ResponseEntity<List<HeatPumpWithLatestMetricsDTO>> {
        val heatPumpsWithMetrics = heatPumpService.getAllHeatPumpsWithLatestMetrics(userName)

        return ResponseEntity.ok(heatPumpsWithMetrics)
    }

    @GetMapping("/{heatPumpId}/metrics")
    fun getHeatPumpWithAllMetrics(@PathVariable heatPumpId: Long): ResponseEntity<HeatPumpWithAllMetricsDTO> {
        val heatPumpWithMetrics = heatPumpService.getHeatPumpWithAllMetrics(heatPumpId)
        return ResponseEntity.ok(heatPumpWithMetrics)
    }
}