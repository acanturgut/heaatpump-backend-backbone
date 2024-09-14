package org.heatpump.backbone.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "metrics")
data class MetricsModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val temperature: Double,

    @Column(nullable = false)
    val efficiency: Double,

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heat_pump_id", nullable = false)
    val heatPump: HeatPumpModel
)

fun MetricsModel.toDTO() = MetricsDTO(
    temperature = temperature,
    efficiency = efficiency,
    timestamp = timestamp
)

data class MetricsDTO(
    val temperature: Double,
    val efficiency: Double,
    val timestamp: LocalDateTime
)