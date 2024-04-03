
# Chesschat: A Real-Time Chat Application

Chesschat is a web application that enables real-time chat functionality. It allows users to connect and communicate seamlessly, making it ideal for various online interactions.

## Features

* **Display LiChess stats:** $stats USERNAME
* **Challenge a player on LiChess:** $challenge USERNAME
* **Private message:** $pm USERNAME MESSAGE

## Local development

### Prerequisites

* Java 17
* Gradle
* Docker

#### Apply Java Code Format

```bash
./gradlew spotlessApply
```

#### Run Docker container like this
```bash
docker run -p 8080:8080 dauer23/chess-chat:latest
``` 
