package redslicedatabase.redslicebackend.features.genapi.dto.outbound;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class ChatGPT4oMiniRequest implements GenApiRequest {

    private String model = "chatgpt4o-mini";
    private List<Message> messages;
    private double temperature;

    @Getter
    @Setter
    @Data
    public static class Message {

        private String role;
        private List<Content> content;

        @Getter
        @Setter
        @Data
        public static class Content {
            private String type;
            private String text;
        }
    }
}
