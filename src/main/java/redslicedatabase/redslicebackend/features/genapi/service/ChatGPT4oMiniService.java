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
import redslicedatabase.redslicebackend.features.genapi.dto.outbound.ChatGPT4oMiniRequest;
import redslicedatabase.redslicebackend.features.genapi.dto.outbound.GenApiRequest;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateMessageDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatGPT4oMiniService implements GenApiService{

    private static final Logger logger = LoggerFactory.getLogger(ChatGPT4oMiniService.class);

    @Autowired
    private GenApiConfig genApiConfig;
    @Autowired
    private TextGeneratingConfig textGeneratingConfig;

    private final RestTemplate restTemplate;

    public ChatGPT4oMiniService(RestTemplate restTemplate) {
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
            logger.error("Error: {}", e.toString());
            throw new Exception(e.toString());
        }
    }

    @Override
    public GenApiRequest genApiRequestConvertDTO(MessageGenerateDTO messageGenerateDTO) {
        // Создаем новый объект ChatGPT4oMiniRequest
        ChatGPT4oMiniRequest chat4oMiniGenApiRequest = new ChatGPT4oMiniRequest();

        // Устанавливаем температуру
        chat4oMiniGenApiRequest.setTemperature(messageGenerateDTO.getTemperature());

        ChatGPT4oMiniRequest.Message contextMessage = new ChatGPT4oMiniRequest.Message();
        ChatGPT4oMiniRequest.Message.Content contextContent = new ChatGPT4oMiniRequest.Message.Content();
        contextContent.setType("text");  // Тип контента (можно менять, если нужно)
        contextContent.setText(messageGenerateDTO.getContext());  // Контекстное сообщение
        contextMessage.setRole("system");  // Тип роли для контекста (можно менять)
        contextMessage.setContent(List.of(contextContent));  // Добавляем контент в сообщение

        // Создаем список для всех сообщений
        List<ChatGPT4oMiniRequest.Message> messageList = new ArrayList<>();
        messageList.add(contextMessage);  // Добавляем контекстное сообщение

        // Преобразуем все сообщения из MessageGenerateDTO в нужный формат
        for (MessageGenerateMessageDTO msgDTO : messageGenerateDTO.getMessages()) {
            ChatGPT4oMiniRequest.Message message = new ChatGPT4oMiniRequest.Message();
            ChatGPT4oMiniRequest.Message.Content content = new ChatGPT4oMiniRequest.Message.Content();
            content.setType("text");  // Тип контента
            content.setText(msgDTO.getText());  // Устанавливаем текст из MessageGenerateMessageDTO

            message.setRole(msgDTO.getRole());  // Устанавливаем роль
            message.setContent(List.of(content));  // Добавляем контент в сообщение

            messageList.add(message);  // Добавляем сообщение в список
        }

        // Устанавливаем все сообщения в ChatGPT4oMiniRequest
        chat4oMiniGenApiRequest.setMessages(messageList);

        return chat4oMiniGenApiRequest;  // Возвращаем конвертированный объект
    }

}
