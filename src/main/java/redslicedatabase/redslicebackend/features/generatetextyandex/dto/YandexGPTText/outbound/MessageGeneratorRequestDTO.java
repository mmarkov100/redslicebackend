package redslicedatabase.redslicebackend.features.generatetextyandex.dto.YandexGPTText.outbound;

/*
DTO класс для отправки запроса на генерацию
 */

import lombok.*;

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
        private ReasoningOptions reasoningOptions;

        public CompletionOptions(boolean stream,
                                 int maxTokens,
                                 double temperature,
                                 ReasoningOptions reasoningOptions) {
            this.stream = stream;
            this.maxTokens = maxTokens;
            this.temperature = temperature;
            this.reasoningOptions = reasoningOptions;
        }

        @Data
        public static class ReasoningOptions {
            private String mode;

            public ReasoningOptions(String mode){
                this.mode = mode;
            }
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

