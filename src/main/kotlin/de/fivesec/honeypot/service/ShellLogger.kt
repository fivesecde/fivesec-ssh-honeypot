package de.fivesec.honeypot.service

import de.fivesec.honeypot.configuration.HoneypotConfiguration
import de.fivesec.honeypot.utils.getHostnameFromIp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ShellLogger(
    honeypotConfiguration: HoneypotConfiguration
) {

    private val logsDirectory: String = honeypotConfiguration.logsDirectory

    fun logSessionStart(sessionId: String, clientIp: String) {
        val sessionFile = getSessionLogFile(sessionId)
        try {
            FileWriter(sessionFile, true).use { writer ->
                val timestamp = getCurrentTimestamp()
                writer.write("=== Session Started at $timestamp ===\n")
                writer.write("Session ID: $sessionId\n")
                writer.write("Client IP: $clientIp\n")
                writer.write("Hostname: ${getHostnameFromIp(clientIp)}\n")
                writer.write("\n")
            }
        } catch (e: IOException) {
            LOG.atError().setMessage("Error writing to session log file").setCause(e).log()
        }
    }

    fun logCommand(sessionId: String, command: String) {
        val sessionFile = getSessionLogFile(sessionId)
        try {
            FileWriter(sessionFile, true).use { writer ->
                val timestamp = getCurrentTimestamp()
                writer.write("[$timestamp] $ $command\n")
            }
        } catch (e: IOException) {
            LOG.atError().setMessage("Error writing command to session log fil").setCause(e).log()
        }
    }

    private fun getSessionLogFile(sessionId: String): File {
        val dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val fileName = "session_${sessionId}_$dateStr.log"
        return File("$logsDirectory/$fileName")
    }

    private fun getCurrentTimestamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}