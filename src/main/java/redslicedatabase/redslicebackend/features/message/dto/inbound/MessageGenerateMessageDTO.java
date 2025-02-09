package redslicedatabase.redslicebackend.features.message.dto.inbound;

/*
DTO сообщения для входящих данных
 */

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageGenerateMessageDTO {

    private String role;
    private String text;

    public MessageGenerateMessageDTO(String role,
                                     String text
    ) {
        this.role = role;
        this.text = text;
    }
}
