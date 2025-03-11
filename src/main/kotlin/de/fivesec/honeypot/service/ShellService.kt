package de.fivesec.honeypot.service

import de.fivesec.honeypot.utils.buildConnectionHash
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.Command
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.*
import java.net.InetSocketAddress
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean

@Service
class ShellService(
    private val shellLogger: ShellLogger
) : Command {

    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var errorStream: OutputStream? = null
    private var exitCallback: ExitCallback? = null
    private var sessionId: String? = null

    private val running = AtomicBoolean(true)
    private var shellThread: Thread? = null

    override fun start(channel: ChannelSession?, environment: Environment?) {
        shellThread = Thread {
            try {
                if (channel == null || environment == null || inputStream == null || outputStream == null || errorStream == null) {
                    LOG.atError().setMessage("Channel or Environment is null").log()
                    return@Thread
                }

                this.sessionId = buildConnectionHash(channel.session.sessionId)
                val clientAddress =
                    (channel.session?.clientAddress as? InetSocketAddress)?.address?.hostAddress ?: "unknown"

                shellLogger.logSessionStart(
                    sessionId = this.sessionId!!,
                    clientIp = clientAddress
                )

                val reader = inputStream!!
                val writer = PrintWriter(OutputStreamWriter(outputStream!!), true)

                writer.print("Welcome to Ubuntu 24.04.2 LTS (GNU/Linux 6.8.0-1021-aws x86_64)\r\n\r\n")
                writer.print("ubuntu@ip-172-31-17-168:~$ ")
                writer.flush()

                val commandBuffer = StringBuilder()

                while (running.get()) {
                    try {
                        if (reader.available() > 0) {
                            val byte = reader.read()
                            if (byte == -1) break

                            when (val char = byte.toChar()) {
                                '\r', '\n' -> {
                                    writer.print("\r\n")
                                    writer.flush()

                                    val command = commandBuffer.toString()
                                    commandBuffer.clear()

                                    if (command.equals("exit", ignoreCase = true) ||
                                        command.equals("logout", ignoreCase = true) ||
                                        command.equals("quit", ignoreCase = true)
                                    ) {
                                        break
                                    }

                                    LOG.atInfo().setMessage("Command received: $command for session: $sessionId").log()

                                    handleCommand(command, writer)

                                    writer.print("ubuntu@ip-172-31-17-168:~$ ")
                                    writer.flush()
                                }

                                '\u0008', '\u007F' -> { // Backspace and Delete
                                    if (commandBuffer.isNotEmpty()) {
                                        commandBuffer.deleteAt(commandBuffer.length - 1)
                                        writer.print("\b \b")
                                        writer.flush()
                                    }
                                }

                                '\t' -> {
                                    writer.print('\t')
                                    writer.flush()
                                }

                                else -> {
                                    commandBuffer.append(char)
                                    writer.print(char)
                                    writer.flush()
                                }
                            }
                        } else {
                            Thread.sleep(10)
                        }
                    } catch (e: IOException) {
                        LOG.atError().setCause(e).setMessage("Error reading input").log()
                        break
                    }
                }

                exitCallback?.onExit(0)
            } catch (ex: Exception) {
                LOG.atError().setCause(ex).setMessage("Error while executing command").log()
                exitCallback?.onExit(1)
            } finally {
                running.set(false)
            }
        }
        shellThread?.start()
    }

    private fun handleCommand(command: String, writer: PrintWriter) {

        shellLogger.logCommand(
            sessionId = this.sessionId!!,
            command = command
        )

        when {
            command.trim().isEmpty() -> {
                // simply OK
            }

            command.startsWith("ls") -> {
                writer.print("Documents  Downloads  Pictures  Desktop\r\n")
            }

            command.startsWith("cd") -> {
                // simply OK
            }

            command.startsWith("pwd") -> {
                writer.print("/home/ubuntu\r\n")
            }

            command.startsWith("whoami") -> {
                writer.print("ubuntu\r\n")
            }

            command.startsWith("date") -> {
                val currentDateTime = ZonedDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")
                writer.print("${currentDateTime.format(formatter)}\r\n")
            }

            command.startsWith("uname") -> {
                writer.print("Linux ip-172-31-17-168 6.8.0-1021-aws #21-Ubuntu SMP PREEMPT_DYNAMIC x86_64 x86_64 x86_64 GNU/Linux\r\n")
            }

            else -> {
                writer.print("Command not found: ${command.split(" ")[0]}\r\n")
            }
        }
        writer.flush()
    }

    override fun destroy(channel: ChannelSession?) {
        running.set(false)
        shellThread?.interrupt()
    }

    override fun setInputStream(inputStream: InputStream?) {
        this.inputStream = inputStream
    }

    override fun setOutputStream(outputStream: OutputStream?) {
        this.outputStream = outputStream
    }

    override fun setErrorStream(errorStream: OutputStream?) {
        this.errorStream = errorStream
    }

    override fun setExitCallback(exitCallback: ExitCallback?) {
        this.exitCallback = exitCallback
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}