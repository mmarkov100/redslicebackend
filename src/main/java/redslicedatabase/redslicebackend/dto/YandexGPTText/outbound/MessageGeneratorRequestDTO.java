package redslicedatabase.redslicebackend.dto.YandexGPTText.outbound;

/*
DTO класс для отправки запроса на генерацию
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageGeneratorRequestDTO {
    private String modelUri;
    private CompletionOptions completionOptions;
    private List<Message> messages;

    @Data
    public static class CompletionOptions {
        private boolean stream;
        private int maxTokens;
        private double temperature;

        public CompletionOptions(boolean stream,
                                 int maxTokens,
                                 double temperature) {
            this.stream = stream;
            this.maxTokens = maxTokens;
            this.temperature = temperature;
        }
    }

    @Data
    public static class Message {
        private String role;
        private String text;

        public Message(
                String role,
                String text) {
            this.role = role;
            this.text = text;
        }
    }
}

