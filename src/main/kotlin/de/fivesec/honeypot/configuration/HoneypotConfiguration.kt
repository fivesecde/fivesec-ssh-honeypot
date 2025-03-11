package de.fivesec.honeypot.configuration

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "honeypot")
data class HoneypotConfiguration(
    val port: Int,
    val hostKeyPath: String,
    val username: String,
    val password: String,
    val logsDirectory: String
)
