package de.fivesec.honeypot.service

import org.apache.sshd.server.SshServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service

@Service
class SshService(
    private var sshServer: SshServer
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        sshServer.start()
        LOG.atInfo().setMessage("Ssh service started").log()
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}