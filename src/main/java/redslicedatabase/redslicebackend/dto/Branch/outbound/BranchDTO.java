package redslicedatabase.redslicebackend.dto.Branch.outbound;

/*
DTO для отправки данных в датабазу
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {
    private Long id;
    private Long chatId;
    private Long parentBranchId;
    private Long messageStartId;
    private boolean isRoot;
    private LocalDateTime dateEdit;
    private LocalDateTime dateCreate;
}
