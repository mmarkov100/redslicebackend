package redslicedatabase.redslicebackend.dto.Message.inbound;

/*
DTO метод для получения данных об генерации текста
 */

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageGenerateDTO {
    private Long branchId;
    private String modelUri;
    private double temperature;
    private String context;
    private List<MessageGenerateMessageDTO> messages;

    public MessageGenerateDTO(
            Long branchId,
            String modelUri,
            double temperature,
            String context,
            List<MessageGenerateMessageDTO> messages
    ) {
        this.branchId = branchId;
        this.modelUri = modelUri;
        this.temperature = temperature;
        this.context = context;
        this.messages = messages;
    }

}
