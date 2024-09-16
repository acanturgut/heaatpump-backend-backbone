package org.heatpump.backbone.repositories

import org.heatpump.backbone.models.HeatPumpModel
import org.heatpump.backbone.models.MetricsModel
import org.springframework.data.jpa.repository.JpaRepository

interface MetricsRepository : JpaRepository<MetricsModel, Long> {
    fun findTopByHeatPumpOrderByTimestampDesc(heatPump: HeatPumpModel): MetricsModel?
    fun findAllByHeatPump(heatPump: HeatPumpModel): List<MetricsModel>
    fun findTopByHeatPump_IdOrderByTimestampDesc(heatPumpId: Long): MetricsModel?
}