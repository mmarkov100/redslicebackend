package redslicedatabase.redslicebackend.features.genapi.dto.outbound;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class DeepSeekV3Request implements GenApiRequest {
    private List<Message> messages;
    private double temperature;

    @Getter
    @Setter
    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
