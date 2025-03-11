package de.fivesec.honeypot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class HoneypotApplication

fun main(args: Array<String>) {
    runApplication<HoneypotApplication>(*args)
}
