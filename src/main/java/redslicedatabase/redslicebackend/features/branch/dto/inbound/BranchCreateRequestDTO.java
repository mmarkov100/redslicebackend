package redslicedatabase.redslicebackend.features.branch.dto.inbound;

/*
DTO для приема от клиента создания новой ветки
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchCreateRequestDTO {
    private Long chatId;
    private Long parentBranchId;
    private Long messageStartId;
}
