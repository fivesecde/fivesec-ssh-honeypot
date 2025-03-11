package de.fivesec.honeypot.utils

import java.net.InetAddress
import java.security.MessageDigest
import java.time.Instant

fun getHostnameFromIp(ipAddress: String): String {
    return try {
        val addr = InetAddress.getByName(ipAddress)
        val hostname = addr.hostName

        if (hostname == ipAddress) {
            "No hostname found for $ipAddress"
        } else {
            hostname
        }
    } catch (e: Exception) {
        "Failed to resolve hostname for $ipAddress: ${e.message}"
        return "unknown"
    }
}

fun buildConnectionHash(
    sessionId: ByteArray,
    timestamp: Instant = Instant.now(),
    algorithm: String = "SHA-256"
): String {
    val digest = MessageDigest.getInstance(algorithm).digest(sessionId + timestamp.toString().toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}