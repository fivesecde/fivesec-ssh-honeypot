package de.fivesec.honeypot.components

import de.fivesec.honeypot.configuration.HoneypotConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
class HoneypotPasswordAuthenticatorTest {

    @Mock
    private lateinit var honeypotConfiguration: HoneypotConfiguration

    @InjectMocks
    private lateinit var passwordAuthenticator: HoneypotPasswordAuthenticator

    @Test
    fun `#HoneypotPasswordAuthenticator - authenticate - should return true`() {

        `when`(honeypotConfiguration.username).thenReturn("test")
        `when`(honeypotConfiguration.password).thenReturn("123456")

        val result = passwordAuthenticator.authenticate("test", "123456", null)

        assertThat(result).isTrue()
    }

    @Test
    fun `#HoneypotPasswordAuthenticator - authenticate - should return false`() {

        `when`(honeypotConfiguration.username).thenReturn("test")
        `when`(honeypotConfiguration.password).thenReturn("123456")

        val result = passwordAuthenticator.authenticate("test", "wrong", null)

        assertThat(result).isFalse()
    }
}