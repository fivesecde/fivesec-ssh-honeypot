package de.fivesec.honeypot.configuration

import de.fivesec.honeypot.components.HoneypotPasswordAuthenticator
import de.fivesec.honeypot.components.HoneypotSessionListener
import de.fivesec.honeypot.service.ShellFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Paths

@Configuration
class SshServerConfig(
    private val shellFactory: ShellFactory,
    private val honeypotConfiguration: HoneypotConfiguration
) {
    @Bean
    fun sshServer(): SshServer {
        LOG.atInfo().setMessage("Init SSH Server on Port: ${honeypotConfiguration.port}").log()

        val server = SshServer.setUpDefaultServer()

        server.disableSessionHeartbeat()
        server.port = honeypotConfiguration.port
        server.keyPairProvider = SimpleGeneratorHostKeyProvider(Paths.get(honeypotConfiguration.hostKeyPath))
        server.passwordAuthenticator = HoneypotPasswordAuthenticator(honeypotConfiguration)
        server.shellFactory = shellFactory
        server.addSessionListener(HoneypotSessionListener())

        return server
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}