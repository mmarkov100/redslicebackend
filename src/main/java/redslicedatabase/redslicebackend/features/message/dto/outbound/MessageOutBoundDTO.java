package redslicedatabase.redslicebackend.features.message.dto.outbound;

/*
DTO сообщения для выходящих данных
 */

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageOutBoundDTO {
    private String uidFirebase;
    private Long branchId;
    private String role;
    private String text;
    private Integer totalTokens;
    private Integer inputTokens;
    private Integer completionTokens;
    private String usedModel;
    private Double cost;

    public MessageOutBoundDTO(String uidFirebase,
                              Long branchId,
                              String role,
                              String text,
                              Integer totalTokens,
                              Integer inputTokens,
                              Integer completionTokens,
                              String usedModel,
                              Double cost
    ) {
        this.uidFirebase = uidFirebase;
        this.branchId = branchId;
        this.role = role;
        this.text = text;
        this.totalTokens = totalTokens;
        this.inputTokens = inputTokens;
        this.completionTokens = completionTokens;
        this.usedModel = usedModel;
        this.cost = cost;
    }
}
