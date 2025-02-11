
# RedSlice Backend Service

RedSlice Backend — это микросервисный бэкенд для веб-приложения **RedSlice**, предназначенного для общения с нейросетью YandexGPT. Сервис реализует функционал работы с чатами, ветками и сообщениями.

## Основной функционал

1. **Пользователь**:
   - Регистрация пользователя.
   - Получение информации о текущем пользователе.
   - Удаление пользователя.

2. **Чаты**:
   - Создание нового чата.
   - Получение списка всех чатов пользователя.
   - Изменение настроек чата.
   - Удаление чата.

3. **Ветки**:
   - Создание новой ветки.
   - Получение списка всех веток чата.
   - Удаление ветки.

4. **Сообщения**:
   - Генерация сообщений через YandexGPT.
   - Сохранение сообщений в базу данных.
   - Получение истории сообщений ветки.

## Технологический стек

- **Язык разработки**: Java
- **Фреймворк**: Spring Boot
- **Аутентификация**: Firebase Auth
- **HTTP-запросы**: RestTemplate
- **Логгирование**: SLF4J + Logback

## API документация

Сервис реализует следующие эндпоинты:

### Пользователь

- **POST /users** — Регистрация пользователя.
- **GET /users** — Получение данных о пользователе.
- **DELETE /users** — Удаление пользователя.

### Чаты

- **POST /chats** — Создание нового чата.
- **GET /chats/user** — Получение всех чатов пользователя.
- **PUT /chats/{chatId}** — Обновление настроек чата.
- **DELETE /chats/{chatId}** — Удаление чата.

### Ветки

- **POST /branches** — Создание новой ветки.
- **GET /branches/chat/{chatId}** — Получение всех веток чата.
- **DELETE /branches/{branchId}** — Удаление ветки.

### Сообщения

- **POST /messages** — Генерация сообщений.
- **POST /messages/branch/{branchId}** — Получение всех сообщений ветки.

Полное описание параметров запроса и примеры можно найти в [API документации](API.md).


## Чтобы начать пользоваться

- Для начала надо клонировать репозиторий к себе в папку
```
git clone https://github.com/mmarkov100/redslicebackend
```
- С firebase получить файл serviceAccountKey.json для вашего проекта в firebase
- Создать файл по пути src/main/java/redslicedatabase/redslicebackend/features/generatetextyandex/config/YandexTextConfig.java
```java
package redslicedatabase.redslicebackend.features.generatetextyandex.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Getter
@Configuration
public class YandexTextConfig {

    private final String apiGenerationKey = "someApiKey";
}

```

Это нужно для защиты связи между генератором и бэком

- Также создайте файл по директории src/main/java/redslicedatabase/redslicebackend/core/config/ApiConfig.java
```java
package redslicedatabase.redslicebackend.core.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@Getter
public class ApiConfig {

    private final String apiDatabaseKey = "someApiKey";
}

```

Также для защиты связи между бэком и базой данных