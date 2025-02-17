package redslicedatabase.redslicebackend.features.genapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.core.config.TextGeneratingConfig;
import redslicedatabase.redslicebackend.features.genapi.config.GenApiConfig;
import redslicedatabase.redslicebackend.features.genapi.dto.inbound.MessageGeneratorResponseGenApiDTO;
import redslicedatabase.redslicebackend.features.genapi.dto.outbound.DeepSeekV3Request;
import redslicedatabase.redslicebackend.features.genapi.dto.outbound.GenApiRequest;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateMessageDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeepSeekV3Service implements GenApiService{

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekV3Service.class);

    @Autowired
    private GenApiConfig genApiConfig;
    @Autowired
    private TextGeneratingConfig textGeneratingConfig;

    private final RestTemplate restTemplate;

    public DeepSeekV3Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Метод для получения ответа на генерацию от сервиса генератора
    @Override
    public MessageGeneratorResponseGenApiDTO generateResponse(GenApiRequest genApiRequest) throws Exception {
        String url = genApiConfig.getGenApiUrl(); //

        HttpHeaders headers = new HttpHeaders();
        headers.set("apiGeneratorKey", textGeneratingConfig.getApiGenerationKey());

        HttpEntity<GenApiRequest> entity = new HttpEntity<>(genApiRequest, headers);

        try {
            ResponseEntity<MessageGeneratorResponseGenApiDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, MessageGeneratorResponseGenApiDTO.class);
            logger.info("Response ID: {}", response);

            return response.getBody();
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }

    @Override
    public GenApiRequest genApiRequestConvertDTO(MessageGenerateDTO messageGenerateDTO) {
        DeepSeekV3Request deepSeekV3Request = new DeepSeekV3Request();

        deepSeekV3Request.setTemperature(messageGenerateDTO.getTemperature());

        DeepSeekV3Request.Message contextMessage = new DeepSeekV3Request.Message();
        contextMessage.setContent(messageGenerateDTO.getContext());  // Контекстное сообщение
        contextMessage.setRole("system");  // Тип роли для контекста (можно менять)

        List<DeepSeekV3Request.Message> messageList = new ArrayList<>();
        messageList.add(contextMessage);  // Добавляем контекстное сообщение

        for (MessageGenerateMessageDTO msgDTO : messageGenerateDTO.getMessages()) {
            DeepSeekV3Request.Message message = new DeepSeekV3Request.Message();

            message.setRole(msgDTO.getRole());  // Устанавливаем роль
            message.setContent(msgDTO.getText());  // Добавляем контент в сообщение

            messageList.add(message);  // Добавляем сообщение в список
        }

        // Устанавливаем все сообщения в GenApiRequest
        deepSeekV3Request.setMessages(messageList);

        return deepSeekV3Request;
    }
}
