package redslicedatabase.redslicebackend.features.message.controller;

/*
Контроллер для сообщений
 */

import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import redslicedatabase.redslicebackend.features.genapi.dto.inbound.MessageGeneratorResponseGenApiDTO;
import redslicedatabase.redslicebackend.features.genapi.dto.outbound.GenApiRequest;
import redslicedatabase.redslicebackend.features.genapi.service.ChatGPT4oMiniService;
import redslicedatabase.redslicebackend.features.genapi.service.DeepSeekV3Service;
import redslicedatabase.redslicebackend.features.genapi.service.GenApiService;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageInboundDTO;
import redslicedatabase.redslicebackend.features.message.repository.MessageRepository;
import redslicedatabase.redslicebackend.features.authfirebase.service.AccountCheckService;
import redslicedatabase.redslicebackend.features.generatetextyandex.service.YandexTextService;
import redslicedatabase.redslicebackend.features.message.service.MessageGenApiService;
import redslicedatabase.redslicebackend.features.message.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private YandexTextService yandexTextService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountCheckService accountCheckService;
    @Autowired
    private DeepSeekV3Service deepSeekV3Service;
    @Autowired
    private ChatGPT4oMiniService chatGPT4oMiniService;
    @Autowired
    private MessageGenApiService messageGenApiService;

    // Метод генерации нового сообщения и сохранения в базу данных
    @PostMapping
    public ResponseEntity<List<MessageInboundDTO>> generateMessage(@RequestHeader String JWTFirebase,
                                                                   @RequestBody MessageGenerateDTO messageGenerateDTO) throws FirebaseAuthException {
        logger.info("POST: message, BranchID: {}", messageGenerateDTO.getBranchId());
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("POST branch: User uidFirebase: {}", uidFirebase);
        return ResponseEntity.ok(messageService.generateMessageProcessing(messageGenerateDTO, uidFirebase)); // Отправляем логику в сервис
    }

    @PostMapping("/genapi")
    public ResponseEntity<?> generateGenApiMessage(@RequestHeader String JWTFirebase,
                                                   @RequestBody MessageGenerateDTO messageGenerateDTO) throws Exception {
        logger.info("POST: message, BranchID: {}", messageGenerateDTO.getBranchId());
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя

        // Проверяем модель и выбираем соответствующий сервис
        GenApiService genApiService = selectGenApiService(messageGenerateDTO.getModel());

        // Конвертируем для отправки
        GenApiRequest genApiRequest = genApiService.genApiRequestConvertDTO(messageGenerateDTO);

        // Генерируем ответ от нейросети
        MessageGeneratorResponseGenApiDTO messageGeneratorResponseGenApiDTO = genApiService.generateResponse(genApiRequest);

        // Отправляем логику отправки для сохранения в бд в сервис
        logger.info("POST branch: User uidFirebase: {}", uidFirebase);
        return ResponseEntity.ok(messageGenApiService.getMessages(messageGeneratorResponseGenApiDTO, messageGenerateDTO, uidFirebase));
    }

    // Метод для выбора подходящего сервиса в зависимости от модели
    private GenApiService selectGenApiService(String model) {
        return switch (model) {
            case "chatgpt4o-mini" -> chatGPT4oMiniService;
            case "deepseek-v3" -> deepSeekV3Service;
            default -> throw new IllegalArgumentException("Unsupported model");
        };
    }

    // Метод для получения всех сообщений ветки
    @PostMapping("/branch/{branchId}")
    public ResponseEntity<List<MessageInboundDTO>> getMessages(@PathVariable Long branchId,
                                                               @RequestHeader String JWTFirebase) throws FirebaseAuthException {
        logger.info("GOT JWT: {}", JWTFirebase);
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя

        return ResponseEntity.ok(messageRepository.getMessagesByBranchId(branchId, uidFirebase)); // Сохраняем в базе данных
    }
}
