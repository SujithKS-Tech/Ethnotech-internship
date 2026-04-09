# PulseChat

PulseChat is a full-stack real-time chat application built with Spring Boot, MySQL, Redis, and STOMP WebSocket.

## Features

- JWT authentication with register and login
- Private chat and group chat creation
- Real-time messaging over WebSocket
- File and image attachment upload`r`n- Voice note upload and playback`r`n- Message editing and soft deletion`r`n- Emoji reactions
- Message history stored in MySQL
- Read receipts backed by the database
- Typing indicators over WebSocket
- Online and offline presence tracking
- Redis caching for presence and recent messages
- Notification storage and delivery
- Modern single-page frontend served by Spring Boot

## Local run

1. Make sure MySQL and Redis are running.
2. Start the app:
   `./mvnw.cmd spring-boot:run`
3. Open `http://localhost:8080`

## Default database settings

- Database: `chat_application`
- Username: `root`
- Password: `Ajay.1@123`

## Docker run

1. Start everything:
   `docker compose up --build`
2. Open `http://localhost:8080`

This starts:
- Spring Boot app
- MySQL 8.4
- Redis 7
- Persistent uploads volume

## Main REST APIs

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users`
- `POST /api/chats`
- `GET /api/chats`
- `GET /api/chats/{chatRoomId}/messages`
- `POST /api/chats/{chatRoomId}/messages`
- `POST /api/chats/{chatRoomId}/attachments` `r`n- `POST /api/chats/{chatRoomId}/voice-notes` `r`n- `PATCH /api/chats/messages/{messageId}` `r`n- `DELETE /api/chats/messages/{messageId}` `r`n- `POST /api/chats/messages/{messageId}/reactions`
- `PATCH /api/chats/{chatRoomId}/read`
- `GET /api/presence/{userId}`
- `GET /api/notifications`
- `PATCH /api/notifications/{notificationId}/read`

## WebSocket routes

- Endpoint: `/ws-chat`
- Send chat message: `/app/chat.send`
- Send typing event: `/app/chat.typing`
- Room updates: `/topic/chat.{chatRoomId}`
- Read receipt updates: `/topic/chat.receipts.{chatRoomId}`
- Typing updates: `/topic/chat.typing.{chatRoomId}`
- User notifications: `/user/queue/notifications`
- Presence updates: `/topic/presence.{userId}`

Pass JWT in the STOMP `Authorization` header as `Bearer <token>`.

## Attachments

Uploaded files are stored in the `uploads` directory locally, or `/app/uploads` inside Docker, and served from `/uploads/...`.
