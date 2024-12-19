package redslicedatabase.redslicebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import redslicedatabase.redslicebackend.dto.Message.inbound.MessageGenerateDTO;
import redslicedatabase.redslicebackend.dto.Message.inbound.MessageGenerateMessageDTO;
import redslicedatabase.redslicebackend.dto.Message.outbound.MessageOutBoundDTO;
import redslicedatabase.redslicebackend.dto.Message.outbound.MessagePairOutboundDTO;
import redslicedatabase.redslicebackend.dto.YandexGPTText.outbound.MessageGeneratorRequestDTO;
import redslicedatabase.redslicebackend.dto.YandexGPTText.inbound.MessageGeneratorResponseDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    private final RestTemplate restTemplate;

    @Autowired
    public MessageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // метод для запроса генерации текста от YandexGPT
    public MessageGeneratorResponseDTO generateMessage(MessageGeneratorRequestDTO requestDTO) {

        // Адрес, где находится сервер генератора
        String yandexUrl = "http://localhost:8082/api/yandex/process";

        // Составляем запрос
        ResponseEntity<MessageGeneratorResponseDTO> response = restTemplate.postForEntity(
                yandexUrl, requestDTO, MessageGeneratorResponseDTO.class
        );

        return response.getBody();
    }

    // Метод конвертации MessageGeneratorRequestDTO в MessageGeneratorResponseDTO для отправки в генератор
    public MessageGeneratorRequestDTO convertToMessageGeneratorResponseDTO(MessageGenerateDTO generateDTO) {
        // Указываем настройки для генерации
        MessageGeneratorRequestDTO.CompletionOptions completionOptions = new MessageGeneratorRequestDTO.CompletionOptions(
                false, // stream - отключен
                2000,  // maxTokens - задаем фиксированно
                generateDTO.getTemperature()
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
        messages.add(0, contextMessage); // Добавляем контекст в начало списка

        // Возвращаем DTO для запроса генерации
        return new MessageGeneratorRequestDTO(
                generateDTO.getModelUri(),
                completionOptions,
                messages
        );
    }

    // Метод конвертации MessageGeneratorResponseDTO в MessageOutBoundDTO для отправки в датабазу
    public MessageOutBoundDTO convertToMessageOutBoundDTO(MessageGeneratorResponseDTO responseDTO) {

        // Извлечение данных из MessageGeneratorResponseDTO
        MessageGeneratorResponseDTO.Message message = responseDTO.getMessage();
        MessageGeneratorResponseDTO.Usage usage = responseDTO.getUsage();

        // Создание и возврат MessageOutBoundDTO
        return new MessageOutBoundDTO(
                message.getRole(),              // Роль сообщения
                message.getText(),              // Текст сообщения
                usage.getTotalTokens(),         // Общее количество токенов
                usage.getInputTextTokens(),     // Количество токенов входного текста
                usage.getCompletionTokens()     // Количество токенов завершения
        );
    }

    // Метод конвертации MessageGenerateMessageDTO в MessageOutBoundDTO для отправки на датабазу
    public MessageOutBoundDTO convertToMessageOutBoundDTO(MessageGenerateMessageDTO generateMessageDTO) {
        return new MessageOutBoundDTO(
                generateMessageDTO.getRole(), // Роль сообщения (например, "user")
                generateMessageDTO.getText(), // Текст сообщения
                0, // Общее количество токенов = 0
                0, // Количество токенов входного текста = 0
                0  // Количество токенов завершения = 0
        );
    }

    // Метод для сборки 2‑х сообщений и отправки на датабазу
    public MessagePairOutboundDTO convertToMessagePairOutboundDTO(MessageGenerateDTO messageGenerateDTO, MessageOutBoundDTO outboundDTO) {

        // Создаем масcив сообщений
        List<MessageOutBoundDTO> messages = new ArrayList<>();

        // Получаем последнее сообщение чата, оно должно быть от пользователя
        MessageOutBoundDTO userMessage = convertToMessageOutBoundDTO(messageGenerateDTO.getMessages().getLast());

        // Добавляем сообщение пользователя, а потом сообщение от генератора в список
        messages.add(userMessage);
        messages.add(outboundDTO);

        return new MessagePairOutboundDTO(
                messageGenerateDTO.getBranchId(),
                messages
        );
    }
}