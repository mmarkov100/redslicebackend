package redslicedatabase.redslicebackend.repository;

/*
Репозиторий для работы с базой данных
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.dto.Message.inbound.MessageInboundDTO;
import redslicedatabase.redslicebackend.dto.Message.outbound.MessagePairOutboundDTO;

import java.util.List;

@Repository
public class MessageRepository {

    private final RestTemplate restTemplate;

    @Autowired
    public MessageRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<MessageInboundDTO> savePairMessages(MessagePairOutboundDTO messages) {
        // Адрес, где находится сервер базы данных и эндпоинт
        String databaseUrl = "http://localhost:8083/messages/pair";

        // Делаем POST-запрос с помощью restTemplate
        ResponseEntity<MessageInboundDTO[]> response = restTemplate.postForEntity(
                databaseUrl, messages, MessageInboundDTO[].class
        );

        // Проверяем успешность запроса и возвращаем данные
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return List.of(response.getBody()); // Преобразуем массив в список
        }
        throw new RuntimeException("Failed to save messages to the database. Response code: " + response.getStatusCode());

    }

    public List<MessageInboundDTO> getMessagesByBranchId(Long branchId) {
        // Адрес, где находится сервер базы данных и эндпоинт
        String databaseUrl = "http://localhost:8083/messages/branch/" + branchId.toString();

        // Делаем GET-запрос с помощью restTemplate
        ResponseEntity<MessageInboundDTO[]> response = restTemplate.getForEntity(
                databaseUrl, MessageInboundDTO[].class
        );

        // Проверяем успешность запроса и возвращаем данные
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return List.of(response.getBody()); // Преобразуем массив в список
        }
        throw new RuntimeException("Failed to get messages from the database. Response code: " + response.getStatusCode());
    }
}
