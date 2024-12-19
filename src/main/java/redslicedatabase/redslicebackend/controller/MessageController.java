package redslicedatabase.redslicebackend.controller;

/*
Контроллер для сообщений
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import redslicedatabase.redslicebackend.dto.Message.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.dto.Message.inbound.MessageInboundDTO;
import redslicedatabase.redslicebackend.dto.Message.outbound.MessageOutBoundDTO;
import redslicedatabase.redslicebackend.dto.Message.outbound.MessagePairOutboundDTO;
import redslicedatabase.redslicebackend.dto.YandexGPTText.inbound.MessageGeneratorResponseDTO;
import redslicedatabase.redslicebackend.dto.YandexGPTText.outbound.MessageGeneratorRequestDTO;
import redslicedatabase.redslicebackend.repository.MessageRepository;
import redslicedatabase.redslicebackend.service.MessageService;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;

    Logger logger = Logger.getLogger(MessageController.class.getName());

    // Метод генерации нового сообщения и сохранения в базу данных
    @PostMapping("/generate")
    public ResponseEntity<List<MessageInboundDTO>> generateMessage(@RequestBody MessageGenerateDTO messageGenerateDTO) {

        // Конвертируем в DTO для отправки запроса в генератор
        MessageGeneratorRequestDTO messageGeneratorRequestDTO = messageService.convertToMessageGeneratorResponseDTO(messageGenerateDTO);
        logger.info("1) Message was converted to MessageGeneratorRequestDTO");

        // Обращаемся в сервис для генерации сообщения
        MessageGeneratorResponseDTO responseDTO = messageService.generateMessage(messageGeneratorRequestDTO);
        logger.info("2) Message was generated");

        // Конвертируем MessageGeneratorResponseDTO в MessageOutBoundDTO для успешной отправки в датабазу
        MessageOutBoundDTO outboundDTO = messageService.convertToMessageOutBoundDTO(responseDTO);
        logger.info("3) Message was converted to MessageOutBoundDTO");

        // Собираем DTO для отправки в датабазу
        MessagePairOutboundDTO messagePairOutboundDTO = messageService.convertToMessagePairOutboundDTO(messageGenerateDTO, outboundDTO);
        logger.info("4) Message was converted to MessagePairOutboundDTO");

        List<MessageInboundDTO> savedMessages = messageRepository.savePairMessages(messagePairOutboundDTO);
        logger.info("5) Message saved");

        return ResponseEntity.ok(savedMessages);
    }

    // Метод для получения всех сообщений ветки
    @GetMapping("/branch/{id}")
    public ResponseEntity<List<MessageInboundDTO>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(messageRepository.getMessagesByBranchId(id));
    }
}
