package redslicedatabase.redslicebackend.features.message.dto.inbound;

/*
DTO сообщения для выходящих данных, приходит от датабазы
 */

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MessageInboundDTO {

    private Long id;
    private Long branchId;
    private String role;
    private String text;
    private Integer totalTokens;
    private Integer inputTokens;
    private Integer completionTokens;
    private LocalDateTime dateCreate;

    public MessageInboundDTO(Long id,
                             Long branchId,
                             String role,
                             String text,
                             Integer totalTokens,
                             Integer inputTokens,
                             Integer completionTokens,
                             LocalDateTime dateCreate
    ) {
        this.id = id;
        this.branchId = branchId;
        this.role = role;
        this.text = text;
        this.totalTokens = totalTokens;
        this.inputTokens = inputTokens;
        this.completionTokens = completionTokens;
        this.dateCreate = dateCreate;
    }
}
