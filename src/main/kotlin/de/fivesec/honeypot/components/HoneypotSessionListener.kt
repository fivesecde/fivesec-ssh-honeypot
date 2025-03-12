package de.fivesec.honeypot.components

import de.fivesec.honeypot.utils.getHostnameFromIp
import org.apache.sshd.common.session.Session
import org.apache.sshd.common.session.SessionListener
import org.apache.sshd.server.session.ServerSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.time.Instant

@Component
class HoneypotSessionListener : SessionListener {


    override fun sessionCreated(session: Session?) {
        if (session != null && session is ServerSession) {
            val clientAddress = (session.clientAddress as? InetSocketAddress)?.address?.hostAddress ?: "unknown"
            val hostname = getHostnameFromIp(clientAddress)

            LOG.atInfo()
                .setMessage("Session created - Session id: ${session.sessionId} from: $clientAddress hostname: $hostname")
                .addKeyValue("address", clientAddress)
                .addKeyValue("sessionID", session.sessionId)
                .addKeyValue("timestamp", Instant.now().toString())
                .addKeyValue("event", "session_created")
                .addKeyValue("hostname", hostname).log()
        }
    }

    override fun sessionClosed(session: Session?) {
        if (session != null && session is ServerSession) {
            val clientAddress = (session.clientAddress as? InetSocketAddress)?.address?.hostAddress ?: "unknown"
            val hostname = getHostnameFromIp(clientAddress)

            LOG.atInfo()
                .setMessage("Session closed - Session id: ${session.sessionId} from: $clientAddress hostname: $hostname")
                .addKeyValue("address", clientAddress)
                .addKeyValue("sessionID", session.sessionId)
                .addKeyValue("timestamp", Instant.now().toString())
                .addKeyValue("event", "session_closed")
                .addKeyValue("hostname", hostname).log()
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}