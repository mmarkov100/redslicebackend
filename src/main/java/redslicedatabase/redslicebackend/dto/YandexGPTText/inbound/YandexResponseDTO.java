package redslicedatabase.redslicebackend.dto.YandexGPTText.inbound;

import lombok.Data;

@Data
public class YandexResponseDTO {
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
