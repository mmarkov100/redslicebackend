package redslicedatabase.redslicebackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.dto.Chat.inbound.ChatCreateRequestDTO;
import redslicedatabase.redslicebackend.dto.Chat.inbound.ChatUpdateRequestDTO;
import redslicedatabase.redslicebackend.dto.Chat.outbound.ChatDTO;

import java.util.List;

@Repository
public class ChatRepository {

    private final RestTemplate restTemplate;

    @Autowired
    public ChatRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Создание нового чата
    public ChatDTO createChat(ChatCreateRequestDTO chatRequest) {
        String databaseUrl = "http://localhost:8083/chats";

        ResponseEntity<ChatDTO> response = restTemplate.postForEntity(
                databaseUrl, chatRequest, ChatDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to create chat. Response code: " + response.getStatusCode());
    }

    // Обновление настроек в чате
    public ChatDTO updateChatSettings(Long chatId, ChatUpdateRequestDTO chatRequest) {
        String databaseUrl = "http://localhost:8083/chats/" + chatId;

        ResponseEntity<ChatDTO> response = restTemplate.exchange(
                databaseUrl, HttpMethod.PUT, new HttpEntity<>(chatRequest), ChatDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to update chat settings. Response code: " + response.getStatusCode());
    }

    // Просмотр всех чатов пользователя
    public List<ChatDTO> getUserChats(Long userId) {
        String databaseUrl = "http://localhost:8083/chats/user/" + userId;

        ResponseEntity<ChatDTO[]> response = restTemplate.getForEntity(databaseUrl, ChatDTO[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return List.of(response.getBody());
        }
        throw new RuntimeException("Failed to get user chats. Response code: " + response.getStatusCode());
    }

    // Просмотр конкретного чата
    public ChatDTO getChatById(Long chatId) {
        String databaseUrl = "http://localhost:8083/chats/" + chatId;

        ResponseEntity<ChatDTO> response = restTemplate.getForEntity(databaseUrl, ChatDTO.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to get chat by ID: " + chatId);
    }

    // Удаление чата
    public void deleteChat(Long chatId) {
        String databaseUrl = "http://localhost:8083/chats/" + chatId;

        restTemplate.delete(databaseUrl);
    }
}
