# Документация API

## Регистрация нового пользователя

### Эндпоинт: POST /users

**Описание:** Создает нового пользователя в системе, используя JWTFirebase и данные из тела запроса.

**Пример запроса:**
```http
POST /users HTTP/1.1
Host: localhost:8080
Content-Type: application/json
JWTFirebase: firebase_jwt_token_example

{
    "email": "newuser@example.com"
}
```

**Пример ответа (успех):**
```json
{
    "id": 1,
    "email": "newuser@example.com",
    "uidFirebase": "firebase_uid_example",
    "totalTokens": 1000,
    "starredChatId": null,
    "dateCreate": "2024-12-23T10:00:00"
}
```

**Пример ответа (ошибка):**
```json
{
    "timestamp": "2024-12-23T10:05:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid Firebase Token",
    "path": "/users"
}
```

---

## Получение информации о пользователе

### Эндпоинт: GET /users

**Описание:** Возвращает информацию о пользователе, используя JWTFirebase.

**Пример запроса:**
```http
GET /users HTTP/1.1
Host: localhost:8080
JWTFirebase: firebase_jwt_token_example
```

**Пример ответа (успех):**
```json
{
    "id": 1,
    "email": "existinguser@example.com",
    "uidFirebase": "firebase_uid_example",
    "totalTokens": 1000,
    "starredChatId": 2,
    "dateCreate": "2024-01-01T12:00:00"
}
```

**Пример ответа (ошибка):**
```json
{
    "timestamp": "2024-12-23T10:10:00",
    "status": 404,
    "error": "Not Found",
    "message": "User not found",
    "path": "/users"
}
```

---

## Работа с чатами

### Создание нового чата

### Эндпоинт: POST /chats

**Описание:** Создает новый чат для пользователя.

**Пример запроса:**
```http
POST /chats HTTP/1.1
Host: localhost:8080
Content-Type: application/json
JWTFirebase: firebase_jwt_token_example

{
    "chatName": "My First Chat",
    "temperature": 0.7,
    "context": "Act as a helpful assistant.",
    "modelUri": "yandexgpt-32k/latest"
}
```

**Пример ответа (успех):**
```json
{
    "id": 1,
    "userId": 10,
    "chatName": "My First Chat",
    "temperature": 0.7,
    "context": "Act as a helpful assistant.",
    "modelUri": "yandexgpt-32k/latest",
    "selectedBranchId": null,
    "dateEdit": "2024-12-23T10:15:30",
    "dateCreate": "2024-12-23T10:15:30"
}
```

---

### Получение списка чатов пользователя

### Эндпоинт: GET /chats/user

**Описание:** Возвращает список всех чатов, связанных с пользователем.

**Пример запроса:**
```http
GET /chats/user HTTP/1.1
Host: localhost:8080
JWTFirebase: firebase_jwt_token_example
```

**Пример ответа (успех):**
```json
[
    {
        "id": 1,
        "userId": 10,
        "chatName": "Chat 1",
        "temperature": 0.7,
        "context": "Assistant mode.",
        "modelUri": "yandexgpt-32k/latest",
        "selectedBranchId": 2,
        "dateEdit": "2024-12-23T11:00:00",
        "dateCreate": "2024-12-23T10:15:30"
    },
    {
        "id": 2,
        "userId": 10,
        "chatName": "Chat 2",
        "temperature": 0.9,
        "context": "Creative writing mode.",
        "modelUri": "yandexgpt-32k/latest",
        "selectedBranchId": null,
        "dateEdit": "2024-12-23T11:30:00",
        "dateCreate": "2024-12-23T11:15:30"
    }
]
```

---

### Обновление настроек чата

### Эндпоинт: PUT /chats/{chatId}

**Описание:** Обновляет настройки указанного чата.

**Пример запроса:**
```http
PUT /chats/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
JWTFirebase: firebase_jwt_token_example

{
    "chatName": "Updated Chat Name",
    "temperature": 0.9,
    "context": "Updated context for the chat.",
    "modelUri": "yandexgpt-32k/latest",
    "selectedBranchId": 3
}
```

**Пример ответа (успех):**
```json
{
    "id": 1,
    "userId": 10,
    "chatName": "Updated Chat Name",
    "temperature": 0.9,
    "context": "Updated context for the chat.",
    "modelUri": "yandexgpt-32k/latest",
    "selectedBranchId": 3,
    "dateEdit": "2024-12-23T12:00:00",
    "dateCreate": "2024-12-23T10:15:30"
}
```

---

### Удаление чата

### Эндпоинт: DELETE /chats/{chatId}

**Описание:** Удаляет указанный чат.

**Пример запроса:**
```http
DELETE /chats/1 HTTP/1.1
Host: localhost:8080
JWTFirebase: firebase_jwt_token_example
```

**Пример ответа (успех):**
```http
HTTP/1.1 204 No Content
```

---

## Работа с ветками

### Создание новой ветки

### Эндпоинт: POST /branches

**Описание:** Создает новую ветку в чате.

**Пример запроса:**
```http
POST /branches HTTP/1.1
Host: localhost:8080
Content-Type: application/json
JWTFirebase: firebase_jwt_token_example

{
    "chatId": 1,
    "parentBranchId": 2,
    "messageStartId": 3
}
```

**Пример ответа (успех):**
```json
{
    "id": 5,
    "chatId": 1,
    "parentBranchId": 2,
    "messageStartId": 3,
    "isRoot": false,
    "dateEdit": "2024-12-23T14:15:00",
    "dateCreate": "2024-12-23T14:15:00"
}
```

---

### Получение всех веток чата

### Эндпоинт: GET /branches/chat/{chatId}

**Описание:** Возвращает список всех веток, связанных с указанным чатом.

**Пример запроса:**
```http
GET /branches/chat/1 HTTP/1.1
Host: localhost:8080
JWTFirebase: firebase_jwt_token_example
```

**Пример ответа (успех):**
```json
[
    {
        "id": 2,
        "chatId": 1,
        "parentBranchId": null,
        "messageStartId": null,
        "isRoot": true,
        "dateEdit": "2024-12-23T14:00:00",
        "dateCreate": "2024-12-23T13:50:00"
    },
    {
        "id": 3,
        "chatId": 1,
        "parentBranchId": 2,
        "messageStartId": 3,
        "isRoot": false,
        "dateEdit": "2024-12-23T14:10:00",
        "dateCreate": "2024-12-23T14:05:00"
    }
]
```

---

### Удаление ветки

### Эндпоинт: DELETE /branches/{branchId}

**Описание:** Удаляет указанную ветку.

**Пример запроса:**
```http
DELETE /branches/3 HTTP/1.1
Host: localhost:8080
JWTFirebase: firebase_jwt_token_example
```

**Пример ответа (успех):**
```http
HTTP/1.1 204 No Content
```

---

## Работа с сообщениями

### Генерация и сохранение сообщения

### Эндпоинт: POST /messages и POST /messages/genapi (позже сделаю один общий)

**Описание:** Генерирует новое сообщение с помощью генератора и сохраняет его в базе данных.

**Пример запроса:**
```http
POST /messages HTTP/1.1
Host: localhost:8080
Content-Type: application/json
JWTFirebase: firebase_jwt_token_example

{
    "branchId": 5,
    "modelUri": "yandexgpt-32k/latest",
    "temperature": 0.7,
    "context": "Act as a helpful assistant.",
    "messages": [
        {
            "role": "user",
            "text": "What is the weather today?"
        }
    ]
}
```

**Пример ответа (успех):**
```json
[
    {
        "id": 1,
        "branchId": 5,
        "role": "user",
        "text": "What is the weather today?",
        "totalTokens": 10,
        "inputTokens": 5,
        "completionTokens": 5,
        "dateCreate": "2024-12-23T15:00:00"
    },
    {
        "id": 2,
        "branchId": 5,
        "role": "assistant",
        "text": "The weather is sunny with a temperature of 25°C.",
        "totalTokens": 15,
        "inputTokens": 5,
        "completionTokens": 10,
        "dateCreate": "2024-12-23T15:01:00"
    }
]
```

---

### Получение всех сообщений ветки

### Эндпоинт: GET /messages/branch/{branchId}

**Описание:** Возвращает список всех сообщений для указанной ветки.

**Пример запроса:**
```http
GET /messages/branch/5 HTTP/1.1
Host: localhost:8080
JWTFirebase: firebase_jwt_token_example
```

**Пример ответа (успех):**
```json
[
    {
        "id": 1,
        "branchId": 5,
        "role": "user",
        "text": "What is the weather today?",
        "totalTokens": 10,
        "inputTokens": 5,
        "completionTokens": 5,
        "dateCreate": "2024-12-23T15:00:00"
    },
    {
        "id": 2,
        "branchId": 5,
        "role": "assistant",
        "text": "The weather is sunny with a temperature of 25°C.",
        "totalTokens": 15,
        "inputTokens": 5,
        "completionTokens": 10,
        "dateCreate": "2024-12-23T15:01:00"
    }
]
```

