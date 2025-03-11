package de.fivesec.honeypot.components

import de.fivesec.honeypot.configuration.HoneypotConfiguration
import de.fivesec.honeypot.utils.getHostnameFromIp
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.session.ServerSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.time.Instant

@Component
class HoneypotPasswordAuthenticator(
    private val honeypotConfiguration: HoneypotConfiguration
) : PasswordAuthenticator {

    override fun authenticate(username: String?, password: String?, session: ServerSession?): Boolean {

        LOG.atInfo().setMessage("Authentication attempt at Honeypot").log()
        val clientAddress = (session?.clientAddress as? InetSocketAddress)?.address?.hostAddress ?: "unknown"


        LOG.atInfo().setMessage("try to resolve remote host").log()
        val resolvedHostname = getHostnameFromIp(clientAddress)

        LOG.atInfo()
            .setMessage("Authentication attempt at Honeypot from: $clientAddress with Username: $username with Password: $password from Hostname: $resolvedHostname")
            .addKeyValue("username", username).addKeyValue("password", password)
            .addKeyValue("session", session?.sessionId).addKeyValue("hostname", resolvedHostname)
            .addKeyValue("timestamp", Instant.now().toString())
            .log()

        if (username == honeypotConfiguration.username && password == honeypotConfiguration.password) {
            LOG.atInfo().setMessage("Authentication successful").log()
            return true
        }

        session?.close()
        return false
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
