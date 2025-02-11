package redslicedatabase.redslicebackend.features.message.repository;

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
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageInboundDTO;
import redslicedatabase.redslicebackend.features.message.dto.outbound.MessagePairOutboundDTO;

import java.util.List;

@Repository
public class MessageRepository {

    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;

    @Autowired
    public MessageRepository(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }

    public List<MessageInboundDTO> savePairMessages(MessagePairOutboundDTO messages) {
        // Адрес, где находится сервер базы данных и эндпоинт
        String databaseUrl = "http://localhost:8083/messages/pair";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<MessagePairOutboundDTO> entity = new HttpEntity<>(messages, headers);

        // Делаем POST-запрос с помощью restTemplate
        ResponseEntity<MessageInboundDTO[]> response = restTemplate.postForEntity(
                databaseUrl, entity, MessageInboundDTO[].class
        );

        // Проверяем успешность запроса и возвращаем данные
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return List.of(response.getBody()); // Преобразуем массив в список
        }
        throw new RuntimeException("Failed to save messages to the database. Response code: " + response.getStatusCode());
    }

    public List<MessageInboundDTO> getMessagesByBranchId(Long branchId, String uidFirebase) {
        // Адрес, где находится сервер базы данных и эндпоинт
        String databaseUrl = "http://localhost:8083/messages/branch/" + branchId.toString() + "/validate?uidFirebase=" + uidFirebase;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers); // Пустое тело для GET-запроса

        try {
            // Делаем GET-запрос с помощью restTemplate
            ResponseEntity<MessageInboundDTO[]> response = restTemplate.exchange(
                    databaseUrl, HttpMethod.GET, entity, MessageInboundDTO[].class
            );

            // Проверяем успешность запроса и возвращаем данные
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return List.of(response.getBody()); // Преобразуем массив в список
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Если чатов нет, возвращаем пустой список
            return List.of();
        }
        throw new RuntimeException("Failed to get messages from the database.");
    }
}
