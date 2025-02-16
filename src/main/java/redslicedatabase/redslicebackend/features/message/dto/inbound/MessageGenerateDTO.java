package redslicedatabase.redslicebackend.features.message.dto.inbound;

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
    private String model;
    private double temperature;
    private String context;
    private List<MessageGenerateMessageDTO> messages;

    public MessageGenerateDTO(
            Long branchId,
            String model,
            double temperature,
            String context,
            List<MessageGenerateMessageDTO> messages
    ) {
        this.branchId = branchId;
        this.model = model;
        this.temperature = temperature;
        this.context = context;
        this.messages = messages;
    }

}
