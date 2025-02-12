package redslicedatabase.redslicebackend.features.message.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.features.generatetextyandex.dto.YandexGPTText.inbound.MessageGeneratorResponseDTO;
import redslicedatabase.redslicebackend.features.generatetextyandex.dto.YandexGPTText.outbound.MessageGeneratorRequestDTO;
import redslicedatabase.redslicebackend.features.generatetextyandex.service.YandexTextService;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateMessageDTO;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageInboundDTO;
import redslicedatabase.redslicebackend.features.message.dto.outbound.MessageOutBoundDTO;
import redslicedatabase.redslicebackend.features.message.dto.outbound.MessagePairOutboundDTO;
import redslicedatabase.redslicebackend.features.message.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final YandexTextService yandexTextService;

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(YandexTextService yandexTextService, RestTemplate ignoredRestTemplate, MessageRepository messageRepository) {
        this.yandexTextService = yandexTextService;
        this.messageRepository = messageRepository;
    }

    // Основной метод для запроса на генерацию текста
    public List<MessageInboundDTO> generateMessageProcessing(MessageGenerateDTO messageGenerateDTO, String uidFirebase) {
        logger.info("Processing message generation for BranchID = {}, User = {}", messageGenerateDTO.getBranchId(), uidFirebase);

        // Конвертируем в DTO для отправки запроса в генератор
        MessageGeneratorRequestDTO messageGeneratorRequestDTO = yandexTextService.convertToMessageGeneratorResponseDTO(messageGenerateDTO);

        // Обращаемся в сервис для генерации сообщения
        MessageGeneratorResponseDTO responseDTO = yandexTextService.generateMessage(messageGeneratorRequestDTO);

        // Конвертируем MessageGeneratorResponseDTO в MessageOutBoundDTO для успешной отправки в датабазу
        MessageOutBoundDTO outboundDTO = convertToMessageOutBoundDTO(responseDTO, uidFirebase, messageGenerateDTO.getBranchId(), messageGenerateDTO.getModelUri());

        // Собираем DTO для отправки в датабазу
        MessagePairOutboundDTO messagePairOutboundDTO = convertToMessagePairOutboundDTO(
                messageGenerateDTO, outboundDTO, uidFirebase, messageGenerateDTO.getBranchId());

        // Сохраняем в базе данных
        logger.debug("Saving message pair to database for BranchID = {}", messageGenerateDTO.getBranchId());
        List<MessageInboundDTO> savedMessages = messageRepository.savePairMessages(messagePairOutboundDTO);
        logger.info("Saved {} messages to the database for BranchID = {}", savedMessages.size(), messageGenerateDTO.getBranchId());

        logger.info("CompletionTokens USER: {}",  savedMessages.get(0).getCompletionTokens());
        logger.info("CompletionTokens BOT: {}", savedMessages.get(1).getCompletionTokens());


        return savedMessages;
    }


    // Метод конвертации MessageGeneratorResponseDTO в MessageOutBoundDTO для отправки в датабазу
    public MessageOutBoundDTO convertToMessageOutBoundDTO(MessageGeneratorResponseDTO responseDTO,
                                                          String uidFirebase,
                                                          Long branchId,
                                                          String modelUri) {
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
                usage.getCompletionTokens(),
                modelUri
        );

        logger.info("Converted MessageOutBoundDTO: Role = {}, Tokens = {}", message.getRole(), usage.getTotalTokens());

        return outboundDTO;
    }


    // Метод для сборки 2‑х сообщений и отправки на датабазу
    public MessagePairOutboundDTO convertToMessagePairOutboundDTO(MessageGenerateDTO messageGenerateDTO,
                                                                  MessageOutBoundDTO outboundDTO,
                                                                  String uidFirebase,
                                                                  Long branchId) {
        logger.debug("Creating MessagePairOutboundDTO for BranchID = {}", branchId);

        // Создаем массив сообщений
        List<MessageOutBoundDTO> messages = new ArrayList<>();

        // Получаем последнее сообщение чата, оно должно быть от пользователя
        MessageOutBoundDTO userMessage = convertToMessageOutBoundDTO(
                messageGenerateDTO.getMessages().get(messageGenerateDTO.getMessages().size()-1),
                uidFirebase,
                branchId);

        // Добавляем сообщение пользователя, а потом сообщение от генератора в список
        messages.add(userMessage);
        messages.add(outboundDTO);

        logger.info("MessagePairOutboundDTO created with {} messages", messages.size());

        return new MessagePairOutboundDTO(
                messageGenerateDTO.getBranchId(),
                messages
        );
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
                0,  // Количество токенов завершения = 0
                "user"
        );
    }
}
