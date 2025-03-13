package de.fivesec.honeypot.utils

import java.net.InetAddress
import java.security.MessageDigest
import java.time.Instant

fun getHostnameFromIp(ipAddress: String): String? {
    return try {
        val addr = InetAddress.getByName(ipAddress)
        val hostname = addr.hostName

        if (hostname == ipAddress) {
            null
        } else {
            hostname
        }
    } catch (e: Exception) {
        return null
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