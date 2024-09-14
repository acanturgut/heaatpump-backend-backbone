package org.heatpump.backbone

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class HearPumpApplication

fun main(args: Array<String>) {
    runApplication<HearPumpApplication>(*args)
}
