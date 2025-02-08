package redslicedatabase.redslicebackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import redslicedatabase.redslicebackend.dto.Message.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.dto.Message.inbound.MessageGenerateMessageDTO;
import redslicedatabase.redslicebackend.dto.Message.inbound.MessageInboundDTO;
import redslicedatabase.redslicebackend.dto.Message.outbound.MessageOutBoundDTO;
import redslicedatabase.redslicebackend.dto.Message.outbound.MessagePairOutboundDTO;
import redslicedatabase.redslicebackend.dto.YandexGPTText.outbound.MessageGeneratorRequestDTO;
import redslicedatabase.redslicebackend.dto.YandexGPTText.inbound.MessageGeneratorResponseDTO;
import redslicedatabase.redslicebackend.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public MessageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private MessageRepository messageRepository;

    // метод для запроса генерации текста от YandexGPT
    public MessageGeneratorResponseDTO generateMessage(MessageGeneratorRequestDTO requestDTO) {

        String yandexUrl = "http://localhost:8085/api/yandex/process";
        logger.info("POST request to YandexGPT: URL={}, Payload={}", yandexUrl, requestDTO);

        try {
            logger.info("Reasoning: {}", requestDTO.getCompletionOptions().getReasoningOptions().getMode());
            // Отправляем запрос к генератору
            ResponseEntity<MessageGeneratorResponseDTO> response = restTemplate.postForEntity(
                    yandexUrl, requestDTO, MessageGeneratorResponseDTO.class
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
                generateDTO.getModelUri(),
                completionOptions,
                messages
        );
    }

    // Метод конвертации MessageGeneratorResponseDTO в MessageOutBoundDTO для отправки в датабазу
    public MessageOutBoundDTO convertToMessageOutBoundDTO(MessageGeneratorResponseDTO responseDTO,
                                                          String uidFirebase,
                                                          Long branchId) {
        logger.debug("Converting MessageGeneratorResponseDTO to MessageOutBoundDTO: BranchID = {}", branchId);

        // Извлечение данных из MessageGeneratorResponseDTO
        MessageGeneratorResponseDTO.Message message = responseDTO.getMessage();
        MessageGeneratorResponseDTO.Usage usage = responseDTO.getUsage();

        // Создает DTO для возврата
        MessageOutBoundDTO outboundDTO = new MessageOutBoundDTO(
                uidFirebase,
                branchId,
                message.getRole(),
                message.getText(),
                usage.getTotalTokens(),
                usage.getInputTextTokens(),
                usage.getCompletionTokens()
        );

        logger.info("Converted MessageOutBoundDTO: Role = {}, Tokens = {}", message.getRole(), usage.getTotalTokens());

        return outboundDTO;
    }

    // Метод конвертации MessageGenerateMessageDTO в MessageOutBoundDTO для отправки на датабазу
    public MessageOutBoundDTO convertToMessageOutBoundDTO(MessageGenerateMessageDTO generateMessageDTO,
                                                          String uidFirebase,
                                                          Long branchId) {
        logger.debug("Creating MessageOutBoundDTO for user input: BranchID = {}", branchId);

        return new MessageOutBoundDTO(
                uidFirebase,
                branchId,
                generateMessageDTO.getRole(), // Роль сообщения (например, "user")
                generateMessageDTO.getText(), // Текст сообщения
                0, // Общее количество токенов = 0
                0, // Количество токенов входного текста = 0
                0  // Количество токенов завершения = 0
        );
    }

    // Метод для сборки 2‑х сообщений и отправки на датабазу
    public MessagePairOutboundDTO convertToMessagePairOutboundDTO(MessageGenerateDTO messageGenerateDTO,
                                                                  MessageOutBoundDTO outboundDTO,
                                                                  String uidFirebase,
                                                                  Long branchId) {
        logger.debug("Creating MessagePairOutboundDTO for BranchID = {}", branchId);

        // Создаем масcив сообщений
        List<MessageOutBoundDTO> messages = new ArrayList<>();

        // Получаем последнее сообщение чата, оно должно быть от пользователя
        MessageOutBoundDTO userMessage = convertToMessageOutBoundDTO(messageGenerateDTO.getMessages().get(messageGenerateDTO.getMessages().size()-1), uidFirebase, branchId);

        // Добавляем сообщение пользователя, а потом сообщение от генератора в список
        messages.add(userMessage);
        messages.add(outboundDTO);

        logger.info("MessagePairOutboundDTO created with {} messages", messages.size());

        return new MessagePairOutboundDTO(
                messageGenerateDTO.getBranchId(),
                messages
        );
    }

    // Основной метод для запроса на генерацию текста
    public List<MessageInboundDTO> generateMessageProcessing(MessageGenerateDTO messageGenerateDTO, String uidFirebase) {
        logger.info("Processing message generation for BranchID = {}, User = {}", messageGenerateDTO.getBranchId(), uidFirebase);

        // Конвертируем в DTO для отправки запроса в генератор
        MessageGeneratorRequestDTO messageGeneratorRequestDTO = convertToMessageGeneratorResponseDTO(messageGenerateDTO);

        // Обращаемся в сервис для генерации сообщения
        MessageGeneratorResponseDTO responseDTO = generateMessage(messageGeneratorRequestDTO);


        // Конвертируем MessageGeneratorResponseDTO в MessageOutBoundDTO для успешной отправки в датабазу
        MessageOutBoundDTO outboundDTO = convertToMessageOutBoundDTO(responseDTO, uidFirebase, messageGenerateDTO.getBranchId());

        // Собираем DTO для отправки в датабазу
        MessagePairOutboundDTO messagePairOutboundDTO = convertToMessagePairOutboundDTO(
                messageGenerateDTO, outboundDTO, uidFirebase, messageGenerateDTO.getBranchId());

        // Сохраняем в базе данных
        logger.debug("Saving message pair to database for BranchID = {}", messageGenerateDTO.getBranchId());
        List<MessageInboundDTO> savedMessages = messageRepository.savePairMessages(messagePairOutboundDTO);
        logger.info("Saved {} messages to the database for BranchID = {}", savedMessages.size(), messageGenerateDTO.getBranchId());


        return savedMessages;
    }
}