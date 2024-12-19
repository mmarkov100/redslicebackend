package redslicedatabase.redslicebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redslicedatabase.redslicebackend.dto.Chat.inbound.ChatCreateRequestDTO;
import redslicedatabase.redslicebackend.dto.Chat.inbound.ChatUpdateRequestDTO;
import redslicedatabase.redslicebackend.dto.Chat.outbound.ChatDTO;
import redslicedatabase.redslicebackend.repository.ChatRepository;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {


    private final ChatRepository chatRepository;

    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    // Метод создания нового чата у пользователя
    @PostMapping
    public ResponseEntity<ChatDTO> createChat(@RequestBody ChatCreateRequestDTO chatRequest) {
        ChatDTO newChat = chatRepository.createChat(chatRequest);
        return ResponseEntity.ok(newChat);
    }

    // Обновление настроек чата
    @PutMapping("/{id}")
    public ResponseEntity<ChatDTO> updateChatSettings(@PathVariable Long id, @RequestBody ChatUpdateRequestDTO chatRequest) {
        ChatDTO updatedChat = chatRepository.updateChatSettings(id, chatRequest);
        return ResponseEntity.ok(updatedChat);
    }

    // Просмотр всех чатов пользователя
    @GetMapping("/user/{id}")
    public ResponseEntity<List<ChatDTO>> getUserChats(@PathVariable Long id) {
        List<ChatDTO> userChats = chatRepository.getUserChats(id);
        return ResponseEntity.ok(userChats);
    }

    // Просмотр конкретного чата
    @GetMapping("/chats/{id}")
    public ResponseEntity<ChatDTO> getChatById(@PathVariable Long id) {
        ChatDTO chat = chatRepository.getChatById(id);
        return ResponseEntity.ok(chat);
    }

    // Удаление чата
    @DeleteMapping("/chats/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        chatRepository.deleteChat(id);
        return ResponseEntity.noContent().build();
    }
}
