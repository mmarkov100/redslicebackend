package redslicedatabase.redslicebackend.features.generatetextyandex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import redslicedatabase.redslicebackend.core.config.TextGeneratingConfig;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateMessageDTO;
import redslicedatabase.redslicebackend.features.generatetextyandex.dto.YandexGPTText.outbound.MessageGeneratorRequestDTO;
import redslicedatabase.redslicebackend.features.generatetextyandex.dto.YandexGPTText.inbound.MessageGeneratorResponseDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class YandexTextService {

    private static final Logger logger = LoggerFactory.getLogger(YandexTextService.class);

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final TextGeneratingConfig textGeneratingConfig;

    @Autowired
    public YandexTextService(RestTemplate restTemplate, TextGeneratingConfig textGeneratingConfig) {
        this.restTemplate = restTemplate;
        this.textGeneratingConfig = textGeneratingConfig;
    }


    // метод для запроса генерации текста от YandexGPT
    public MessageGeneratorResponseDTO generateMessage(MessageGeneratorRequestDTO requestDTO) {

        String yandexUrl = "http://localhost:8085/api/yandex/process";
        logger.info("POST request to YandexGPT: URL={}, Payload={}", yandexUrl, requestDTO);

        try {
            logger.info("Reasoning: {}", requestDTO.getCompletionOptions().getReasoningOptions().getMode());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apiGeneratorKey", textGeneratingConfig.getApiGenerationKey());
            HttpEntity<MessageGeneratorRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

            // Отправляем запрос к генератору
            ResponseEntity<MessageGeneratorResponseDTO> response = restTemplate.postForEntity(
                    yandexUrl, entity, MessageGeneratorResponseDTO.class
            );

            // Проверяем успешность ответа
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("YandexGPT response received successfully.");
                return response.getBody();
            } else {
                logger.error("YandexGPT returned a non-successful response: Status={}, Body={}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Error in YandexGPT response: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // Обработка ошибки клиента (например, 4xx ошибки)
            logger.error("Client error while calling YandexGPT: Status={}, Message={}.",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Client error during YandexGPT request: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // Обработка ошибки сервера (например, 5xx ошибки)
            logger.error("Server error while calling YandexGPT: Status={}, Message={}.",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Server error during YandexGPT request: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            // Обработка ошибок доступа к ресурсу (например, тайм-аут или невозможность подключения)
            logger.error("Resource access error while calling YandexGPT: Message={}.", e.getMessage(), e);
            throw new RuntimeException("Resource access error during YandexGPT request: " + e.getMessage(), e);
        } catch (Exception e) {
            // Общая обработка ошибок
            logger.error("Unexpected error while calling YandexGPT: Message={}.", e.getMessage(), e);
            throw new RuntimeException("Unexpected error during YandexGPT request: " + e.getMessage(), e);
        }
    }

    // Метод конвертации MessageGeneratorRequestDTO в MessageGeneratorResponseDTO для отправки в генератор
    public MessageGeneratorRequestDTO convertToMessageGeneratorResponseDTO(MessageGenerateDTO generateDTO) {
        logger.debug("Converting MessageGenerateDTO to MessageGeneratorRequestDTO: BranchID = {}", generateDTO.getBranchId());

        // Указываем настройки для генерации
        MessageGeneratorRequestDTO.CompletionOptions completionOptions = new MessageGeneratorRequestDTO.CompletionOptions(
                false, // stream - отключен
                2000,  // maxTokens - задаем фиксированно
                generateDTO.getTemperature(), // задаем температуру
                new MessageGeneratorRequestDTO.CompletionOptions.ReasoningOptions("DISABLED") // Режим размышления отключен
        );

        // Конвертируем список сообщений в изменяемый список
        List<MessageGeneratorRequestDTO.Message> messages = new ArrayList<>();
        for (MessageGenerateMessageDTO msg : generateDTO.getMessages()) {
            messages.add(new MessageGeneratorRequestDTO.Message(msg.getRole(), msg.getText()));
        }

        // Добавляем контекст как первое сообщение
        MessageGeneratorRequestDTO.Message contextMessage = new MessageGeneratorRequestDTO.Message(
                "system", generateDTO.getContext()
        );
        messages.add(0,contextMessage); // Добавляем контекст в начало списка

        logger.info("Converted to MessageGeneratorRequestDTO with {} messages", messages.size());

        // Возвращаем DTO для запроса генерации
        return new MessageGeneratorRequestDTO(
                generateDTO.getModel(),
                completionOptions,
                messages
        );
    }

}