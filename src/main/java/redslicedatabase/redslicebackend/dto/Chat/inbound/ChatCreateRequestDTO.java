package redslicedatabase.redslicebackend.dto.Chat.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreateRequestDTO {
    private Long userId;
    private String chatName;
    private double temperature;
    private String context;
    private String modelUri;
}
