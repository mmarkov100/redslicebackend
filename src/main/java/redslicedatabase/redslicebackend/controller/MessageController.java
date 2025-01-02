package redslicedatabase.redslicebackend.controller;

/*
Контроллер для сообщений
 */

import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import redslicedatabase.redslicebackend.dto.Message.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.dto.Message.inbound.MessageInboundDTO;
import redslicedatabase.redslicebackend.repository.MessageRepository;
import redslicedatabase.redslicebackend.service.AccountCheckService;
import redslicedatabase.redslicebackend.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountCheckService accountCheckService;

    // Метод генерации нового сообщения и сохранения в базу данных
    @PostMapping
    public ResponseEntity<List<MessageInboundDTO>> generateMessage(@RequestHeader String JWTFirebase,
                                                                   @RequestBody MessageGenerateDTO messageGenerateDTO) throws FirebaseAuthException {
        logger.info("POST: message, BranchID: {}", messageGenerateDTO.getBranchId());
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("POST branch: User uidFirebase: {}", uidFirebase);
        return ResponseEntity.ok(messageService.generateMessageProcessing(messageGenerateDTO, uidFirebase)); // Отправляем логику в сервис
    }

    // Метод для получения всех сообщений ветки
    @PostMapping("/branch/{branchId}")
    public ResponseEntity<List<MessageInboundDTO>> getMessages(@PathVariable Long branchId,
                                                               @RequestHeader String JWTFirebase) throws FirebaseAuthException {
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя

        return ResponseEntity.ok(messageRepository.getMessagesByBranchId(branchId, uidFirebase)); // Сохраняем в базе данных
    }
}
