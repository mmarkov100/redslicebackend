package redslicedatabase.redslicebackend.dto.User.outbound;

/*
DTO для отправки запроса создания на датабазу
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDatabaseDTO {
    private String email;
    private String uidFirebase;
    private int totalTokens;
}
