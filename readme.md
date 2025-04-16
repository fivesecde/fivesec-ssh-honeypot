# Fivesec SSH Honeypot

Made with ❤️ by [@fivesec](https://fivesec.de)

## About

Fivesec SSH Honeypot is a lightweight and easy-to-use SSH honeypot that logs all login attempts and session activity for forensic analysis. It provides a fake shell for intruders and captures all executed commands in a structured format.

## Features
- **SSH Honeypot**: Captures all login attempts with detailed metadata (IP, username, password, timestamp, hostname lookup)
- **Fake Shell**: Provides a minimal fake shell (not a real system shell!) interface for intruders
- **Comprehensive Logging**: Logs all executed commands and interactions per session
- **Dockerized Deployment**: Ready-to-use with Docker for seamless setup

## Example Logs

### Login Attempt

```text
2025-03-11T09:00:21.895Z  INFO 1 --- [honeypot] [sshd-SshServer[797fcf9](port=2222)-nio2-thread-3] .HoneypotPasswordAuthenticator$Companion : Authentication attempt at Honeypot
2025-03-11T09:00:21.896Z  INFO 1 --- [honeypot] [sshd-SshServer[797fcf9](port=2222)-nio2-thread-3] .HoneypotPasswordAuthenticator$Companion : try to resolve remote host
2025-03-11T09:00:21.910Z  INFO 1 --- [honeypot] [sshd-SshServer[797fcf9](port=2222)-nio2-thread-3] .HoneypotPasswordAuthenticator$Companion : Authentication attempt at Honeypot from: 77.177.57.206 with Username: ubuntu2 with Password: asd from Hostname: dynamic-077-177-057-206.77.177.pool.telefonica.de
2025-03-11T09:00:21.911Z  INFO 1 --- [honeypot] [sshd-SshServer[797fcf9](port=2222)-nio2-thread-3] .f.h.c.HoneypotSessionListener$Companion : Session closed - Session id: [B@25113d8a
```

### Session Activity

```text
=== Session Started at 2025-03-10 20:41:48 ===
Session ID: 73a7fdb3e8f03842cdcc99314473a3de992d5dedbc9e0de336c89f99dd82ce9a
Client IP: 77.176.12.174
Hostname: dynamic-077-176-012-174.77.176.pool.telefonica.de

[2025-03-10 20:41:50] $ whoami
[2025-03-10 20:41:51] $ date
```


## Setup & Configuration

### Docker Compose Example
Here's a docker compose example that you can adjust for your needs and it will build the honeypot direct from source

```yaml
services:
  honeypot:
    image: fivesec-honeypot:latest
    restart: unless-stopped
    build:
      context: ./honeypot
    ports:
      - "22:2222"
    environment:
      - logging.level.root=INFO
      - logging.file.path=/tmp/honeypot-logs
      - honeypot.username=some-wildy-used-username
      - honeypot.password=some-wildy-used-password
      - honeypot.host-key-path=/tmp/honeypot-logs/honeypot.ser
      - honeypot.logs-directory=/tmp/honeypot-logs/sessions
    volumes:
      - ./local-log-path:/tmp/honeypot-logs
```

### First-Time Setup

Create a log folder to mount as a volume for persistent logs:
```bash
mkdir local-log-path
```

In order to access the logs from the container user (1001) make sure to change the permissions of the log folder:
```bash
chown 1001:<sys-user-or-whatever> ./local-log-path
chmod -R 755 ./local-log-path
```

To build and start the honeypot, run:
```bash
docker compose up -d --build && docker compose logs -f
```

### Expected Output
Upon successful startup, you should see logs similar to:
```text
{"@timestamp":"2025-03-11T14:19:27.042345+01:00","message":"Started HoneypotApplicationKt in 1.139 seconds"}
{"@timestamp":"2025-03-11T14:19:27.054507+01:00","message":"SSH service started"}
```

## Additional Help
If you need help/have questions, feel free to get in touch with us at every time by sending us an email
to security@fivesec.de or visit our homepage https://fivesec.de :-)

## Contributing
We welcome contributions! Feel free to submit issues, feature requests, or pull requests.
