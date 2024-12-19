package redslicedatabase.redslicebackend.dto.Chat.outbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private Long id;
    private Long userId;
    private String chatName;
    private double temperature;
    private String context;
    private String modelUri;
    private Long selectedBranchId;
    private LocalDateTime dateEdit;
    private LocalDateTime dateCreate;
}
