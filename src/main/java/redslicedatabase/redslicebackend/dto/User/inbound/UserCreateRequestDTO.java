package redslicedatabase.redslicebackend.dto.User.inbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDTO {
    private String email;
    private String uidFirebase;
    private int totalTokens;
}
