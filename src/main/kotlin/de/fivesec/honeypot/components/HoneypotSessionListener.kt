package de.fivesec.honeypot.components

import org.apache.sshd.common.session.Session
import org.apache.sshd.common.session.SessionListener
import org.apache.sshd.server.session.ServerSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.InetSocketAddress

@Component
class HoneypotSessionListener : SessionListener {


    override fun sessionCreated(session: Session?) {
        if (session != null && session is ServerSession) {
            val clientAddress = (session.clientAddress as? InetSocketAddress)?.address?.hostAddress ?: "unknown"
            LOG.atInfo().setMessage("Session created - Session id: ${session.sessionId} from: $clientAddress")
                .addKeyValue("address", clientAddress).addKeyValue("sessionID", session.sessionId).log()
        }
    }

    override fun sessionClosed(session: Session?) {
        if (session != null && session is ServerSession) {
            val clientAddress = (session.clientAddress as? InetSocketAddress)?.address?.hostAddress ?: "unknown"
            LOG.atInfo().setMessage("Session closed - Session id: ${session.sessionId} from: $clientAddress")
                .addKeyValue("address", clientAddress).addKeyValue("sessionID", session.sessionId).log()
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}