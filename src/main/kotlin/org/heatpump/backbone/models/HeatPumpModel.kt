package org.heatpump.backbone.models

import jakarta.persistence.*

@Entity
@Table(name = "heat_pumps")
data class HeatPumpModel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @OneToMany(mappedBy = "heatPump", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val metrics: List<MetricsModel> = mutableListOf()
)

data class HeatPumpWithLatestMetricsDTO(
    val heatPumpName: String,
    val latestMetrics: MetricsDTO?
)

data class HeatPumpWithAllMetricsDTO(
    val heatPumpName: String,
    val metrics: List<MetricsDTO>
)