package redslicedatabase.redslicebackend.dto.YandexGPTText.inbound;

/*
DTO класс для получения сгенерированного сообщения от генератора
 */

import lombok.Data;

@Data
public class MessageGeneratorResponseDTO {
    private Message message;
    private Usage usage;

    @Data
    public static class Message {
        private String role;
        private String text;
    }

    @Data
    public static class Usage {
        private int inputTextTokens;
        private int totalTokens;
        private int completionTokens;
    }
}
