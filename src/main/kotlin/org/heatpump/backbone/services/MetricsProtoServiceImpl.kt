package org.heatpump.backbone.services

import io.grpc.stub.StreamObserver
import org.heatpump.backbone.grpc.MetricsProtoServiceGrpc
import org.heatpump.backbone.grpc.MetricsRequest
import org.heatpump.backbone.grpc.MetricsResponse
import org.heatpump.backbone.repositories.MetricsRepository
import org.lognet.springboot.grpc.GRpcService
import java.time.ZoneOffset

@GRpcService
class MetricsProtoServiceImpl(private val metricsRepository: MetricsRepository) : MetricsProtoServiceGrpc.MetricsProtoServiceImplBase() {

    override fun getMetrics(request: MetricsRequest?, responseObserver: StreamObserver<MetricsResponse>?) {

        if (request?.heatPumpId == null) {
            responseObserver?.onError(Exception("Heat pump ID is required"))
            return
        }

        val heatPumpId = request.heatPumpId

        val heatPump = metricsRepository.findTopByHeatPump_IdOrderByTimestampDesc(heatPumpId) ?: throw Exception("No metrics found for heat pump with ID $heatPumpId")

        val response = MetricsResponse.newBuilder()
            .setHeatPumpId(heatPumpId)
            .setTemperature(heatPump.temperature)
            .setEfficiency(heatPump.efficiency)
            .setUnixTimeStamp(heatPump.timestamp.toEpochSecond(ZoneOffset.UTC))
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}