package de.fivesec.honeypot.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.net.InetAddress
import java.util.*

class CommonTest {

    @Test
    fun `#Common - getHostnameFromIp - should return hostname`() {

        val mockInetAddress = mock(InetAddress::class.java)
        `when`(mockInetAddress.hostName).thenReturn("localhost")

        val hostname = getHostnameFromIp(
            ipAddress = "127.0.0.1"
        )

        assertThat(hostname).isEqualTo("localhost")
        
    }

    @Test
    fun `#Common - buildConnectionHash - should return string hash`() {
        assertDoesNotThrow {
            buildConnectionHash(
                sessionId = UUID.randomUUID().toString().encodeToByteArray()
            )
        }
    }
}