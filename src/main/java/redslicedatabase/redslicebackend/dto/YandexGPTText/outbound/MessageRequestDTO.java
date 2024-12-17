package redslicedatabase.redslicebackend.dto.YandexGPTText.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDTO {
    private String modelUri;
    private CompletionOptions completionOptions;
    private List<Message> messages;

    @Data
    public static class CompletionOptions {
        private boolean stream;
        private int maxTokens;
        private double temperature;
    }

    @Data
    public static class Message {
        private String role;
        private String text;
    }
}

