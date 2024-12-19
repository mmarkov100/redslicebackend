package redslicedatabase.redslicebackend.dto.Chat.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatUpdateRequestDTO {
    private String chatName;
    private double temperature;
    private String context;
    private String modelUri;
    private Long selectedBranchId;
}
