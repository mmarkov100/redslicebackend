package redslicedatabase.redslicebackend.dto.Message.outbound;

/*
DTO сообщения для выходящих данных
 */

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageOutBoundDTO {

    private String role;
    private String text;
    private Integer totalTokens;
    private Integer inputTokens;
    private Integer completionTokens;

    public MessageOutBoundDTO(String role,
                              String text,
                              Integer totalTokens,
                              Integer inputTokens,
                              Integer completionTokens
    ) {
        this.role = role;
        this.text = text;
        this.totalTokens = totalTokens;
        this.inputTokens = inputTokens;
        this.completionTokens = completionTokens;
    }
}
