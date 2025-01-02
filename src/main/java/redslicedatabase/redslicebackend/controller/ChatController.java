package redslicedatabase.redslicebackend.controller;

/*
Контроллера для чата
 */

import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redslicedatabase.redslicebackend.dto.Chat.inbound.ChatCreateRequestDTO;
import redslicedatabase.redslicebackend.dto.Chat.inbound.ChatUpdateRequestDTO;
import redslicedatabase.redslicebackend.dto.Chat.outbound.ChatDTO;
import redslicedatabase.redslicebackend.repository.ChatRepository;
import redslicedatabase.redslicebackend.service.AccountCheckService;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private AccountCheckService accountCheckService;

    // Метод создания нового чата у пользователя
    @PostMapping
    public ResponseEntity<ChatDTO> createChat(@RequestHeader String JWTFirebase,
                                              @RequestBody ChatCreateRequestDTO chatRequest) throws FirebaseAuthException {
        logger.info("POST: chat");
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("POST chat: User uidFirebase: {}", uidFirebase);
        ChatDTO newChat = chatRepository.createChat(chatRequest, uidFirebase); // Создаем чат
        logger.info("POST chat: Chat created, UID: {}, ChatID: {}", uidFirebase, newChat.getId());
        return ResponseEntity.ok(newChat);
    }

    // Просмотр информации всех чатов пользователя
    @PostMapping("/user")
    public ResponseEntity<List<ChatDTO>> getUserChats(@RequestHeader String JWTFirebase) throws FirebaseAuthException {
        logger.info("GET: chats");
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("GET chats: User uidFirebase: {}", uidFirebase);
        List<ChatDTO> userChats = chatRepository.getUserChats(uidFirebase); // Получаем чаты
        logger.info("GET chats: Chats got, UID: {}", uidFirebase);
        return ResponseEntity.ok(userChats);
    }

//    // Просмотр информации конкретного чата
//    @GetMapping("/{chatId}")
//    public ResponseEntity<ChatDTO> getChatById(@PathVariable Long chatId,
//                                               @RequestHeader String JWTFirebase) throws FirebaseAuthException {
//        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase);
//        ChatDTO chat = chatRepository.getChatById(chatId);
//        return ResponseEntity.ok(chat);
//    }

    // Обновление настроек чата
    @PutMapping("/{chatId}")
    public ResponseEntity<ChatDTO> updateChatSettings(@PathVariable Long chatId,
                                                      @RequestHeader String JWTFirebase,
                                                      @RequestBody ChatUpdateRequestDTO chatRequest) throws FirebaseAuthException {
        logger.info("PUT: chat, chatID: {}", chatId);
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("PUT chat: User uidFirebase: {}", uidFirebase);
        ChatDTO updatedChat = chatRepository.updateChatSettings(chatId, chatRequest, uidFirebase); // Обновляем настройки чата
        logger.info("PUT chat: Chat updated, UID: {}, ChatID: {}", uidFirebase, updatedChat.getId());
        return ResponseEntity.ok(updatedChat);
    }

    // Удаление чата
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId,
                                           @RequestHeader String JWTFirebase) throws FirebaseAuthException {
        logger.info("DELETE: chat, chatID: {}", chatId);
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("DELETE chat: User uidFirebase: {}", uidFirebase);
        chatRepository.deleteChat(chatId, uidFirebase);
        logger.info("DELETE chat: Chat deleted, UID: {}", uidFirebase);
        return ResponseEntity.noContent().build();
    }
}
