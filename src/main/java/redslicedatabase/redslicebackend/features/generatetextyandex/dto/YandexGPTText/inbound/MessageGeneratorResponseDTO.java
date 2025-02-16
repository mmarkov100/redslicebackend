package redslicedatabase.redslicebackend.features.generatetextyandex.dto.YandexGPTText.inbound;

/*
DTO класс для получения сгенерированного сообщения от генератора
 */

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MessageGeneratorResponseDTO {
    private Message message;
    private Usage usage;

    @Data
    @Getter
    @Setter
    public static class Message {
        private String role;
        private String text;
    }

    @Data
    @Getter
    @Setter
    public static class Usage {
        private int inputTextTokens;
        private int totalTokens;
        private int completionTokens;
        private double cost;
    }
}
