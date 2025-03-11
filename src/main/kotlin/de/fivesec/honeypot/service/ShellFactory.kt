package de.fivesec.honeypot.service

import de.fivesec.honeypot.configuration.HoneypotConfiguration
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.Command
import org.apache.sshd.server.shell.ShellFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ShellFactory(
    private val honeypotConfiguration: HoneypotConfiguration
) : ShellFactory {
    override fun createShell(channel: ChannelSession): Command {
        LOG.atInfo().setMessage("create new shell session").log()
        return ShellService(
            ShellLogger(honeypotConfiguration)
        )
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}