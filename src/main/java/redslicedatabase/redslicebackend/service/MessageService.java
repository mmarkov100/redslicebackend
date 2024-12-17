package redslicedatabase.redslicebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import redslicedatabase.redslicebackend.dto.YandexGPTText.outbound.MessageRequestDTO;
import redslicedatabase.redslicebackend.dto.YandexGPTText.inbound.YandexResponseDTO;

@Service
public class MessageService {

    private final RestTemplate restTemplate;

    @Autowired
    public MessageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public YandexResponseDTO generateMessage(MessageRequestDTO requestDTO) {
        String yandexUrl = "http://localhost:8082/api/yandex/process";

        ResponseEntity<YandexResponseDTO> response = restTemplate.postForEntity(
                yandexUrl, requestDTO, YandexResponseDTO.class
        );

        return response.getBody();
    }
}