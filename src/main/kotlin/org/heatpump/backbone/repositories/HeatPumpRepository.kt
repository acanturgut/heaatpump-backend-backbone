package org.heatpump.backbone.repositories

import org.heatpump.backbone.models.HeatPumpModel
import org.heatpump.backbone.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface HeatPumpRepository : JpaRepository<HeatPumpModel, Long> {
    fun findByUser(user: User): List<HeatPumpModel>
}