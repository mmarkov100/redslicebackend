package redslicedatabase.redslicebackend.dto.User.outbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String uidFirebase;
    private int totalTokens;
    private Long starredChatId;
    private LocalDateTime dateCreate;
}
