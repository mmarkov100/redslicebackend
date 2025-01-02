package redslicedatabase.redslicebackend.dto.Branch.outbound;

/*
DTO для датабазы, создания новой ветки
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchCreateDatabaseDTO {
    private String uidFirebase;
    private Long chatId;
    private Long parentBranchId;
    private Long messageStartId;
}
