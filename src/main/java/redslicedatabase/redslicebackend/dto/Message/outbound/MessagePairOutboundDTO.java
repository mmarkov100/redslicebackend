package redslicedatabase.redslicebackend.dto.Message.outbound;

/*
DTO для отправки запроса на сохранение в датабазу
 */

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessagePairOutboundDTO {
    private Long branchId;
    private List<MessageOutBoundDTO> messages;

    public MessagePairOutboundDTO(Long branchId,
                                  List<MessageOutBoundDTO> messages) {
        this.branchId = branchId;
        this.messages = messages;
    }
}
