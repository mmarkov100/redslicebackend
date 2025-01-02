package redslicedatabase.redslicebackend.dto.Chat.outbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreateDatabaseDTO {
    private String uidFirebase;
    private String chatName;
    private double temperature;
    private String context;
    private String modelUri;
}