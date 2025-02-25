package redslicedatabase.redslicebackend.features.chat.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.core.config.ApiConfig;
import redslicedatabase.redslicebackend.features.chat.dto.inbound.ChatCreateRequestDTO;
import redslicedatabase.redslicebackend.features.chat.dto.inbound.ChatUpdateRequestDTO;
import redslicedatabase.redslicebackend.features.chat.dto.outbound.ChatCreateDatabaseDTO;
import redslicedatabase.redslicebackend.features.chat.dto.outbound.ChatDTO;
import redslicedatabase.redslicebackend.features.chat.dto.outbound.ChatUpdateDatabaseDTO;

import java.util.List;

@Repository
public class ChatRepository {

    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;

    @Autowired
    public ChatRepository(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }

    // Создание нового чата
    public ChatDTO createChat(ChatCreateRequestDTO chatRequest, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/chats";

        ChatCreateDatabaseDTO chatCreateDatabaseDTO = new ChatCreateDatabaseDTO();
        chatCreateDatabaseDTO.setUidFirebase(uidFirebase);
        chatCreateDatabaseDTO.setChatName(chatRequest.getChatName());
        chatCreateDatabaseDTO.setTemperature(chatRequest.getTemperature());
        chatCreateDatabaseDTO.setContext(chatRequest.getContext());
        chatCreateDatabaseDTO.setModelUri(chatRequest.getModelUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<ChatCreateDatabaseDTO> entity = new HttpEntity<>(chatCreateDatabaseDTO, headers);

        ResponseEntity<ChatDTO> response = restTemplate.postForEntity(
                databaseUrl, entity, ChatDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to create chat. Response code: " + response.getStatusCode());
    }

    // Обновление настроек в чате
    public ChatDTO updateChatSettings(Long chatId, ChatUpdateRequestDTO chatRequest, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/chats/" + chatId;

        ChatUpdateDatabaseDTO chatUpdateDatabaseDTO = new ChatUpdateDatabaseDTO();
        chatUpdateDatabaseDTO.setUidFirebase(uidFirebase);
        chatUpdateDatabaseDTO.setChatName(chatRequest.getChatName());
        chatUpdateDatabaseDTO.setTemperature(chatRequest.getTemperature());
        chatUpdateDatabaseDTO.setContext(chatRequest.getContext());
        chatUpdateDatabaseDTO.setModelUri(chatRequest.getModelUri());
        chatUpdateDatabaseDTO.setSelectedBranchId(chatRequest.getSelectedBranchId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<ChatUpdateDatabaseDTO> entity = new HttpEntity<>(chatUpdateDatabaseDTO, headers);

        ResponseEntity<ChatDTO> response = restTemplate.exchange(
                databaseUrl, HttpMethod.PUT, entity, ChatDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to update chat settings. Response code: " + response.getStatusCode());
    }

    // Просмотр всех чатов пользователя
    public List<ChatDTO> getUserChats(String uidFirebase) {
        String databaseUrl = "http://localhost:8083/chats/user/uid/" + uidFirebase;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers); // Пустое тело для GET-запроса

        try {
            ResponseEntity<ChatDTO[]> response = restTemplate.exchange(
                    databaseUrl, HttpMethod.GET, entity, ChatDTO[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return List.of(response.getBody());
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Если чатов нет, возвращаем пустой список
            return List.of();
        }
        throw new RuntimeException("Failed to get user chats");
    }

    // Удаление чата
    public void deleteChat(Long chatId, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/chats/" + chatId + "/validate?uidFirebase=" + uidFirebase;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers); // Пустое тело для DELETE-запроса

        restTemplate.exchange(databaseUrl, HttpMethod.DELETE, entity, Void.class);
    }
}
