package redslicedatabase.redslicebackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.dto.User.inbound.UserCreateRequestDTO;
import redslicedatabase.redslicebackend.dto.User.outbound.UserDTO;
import redslicedatabase.redslicebackend.dto.User.outbound.UserCreateDatabaseDTO;

@Repository
public class UserRepository {

    private final RestTemplate restTemplate;

    @Autowired
    public UserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Метод для обращения к бд создание нового пользователя
    public UserDTO createUser(UserCreateRequestDTO userRequest, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/users";

        UserCreateDatabaseDTO userCreateDatabaseDTO = new UserCreateDatabaseDTO();
        userCreateDatabaseDTO.setEmail(userRequest.getEmail());
        userCreateDatabaseDTO.setUidFirebase(uidFirebase);
        userCreateDatabaseDTO.setTotalTokens(10000);

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(
                databaseUrl, userCreateDatabaseDTO, UserDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to create user. Response code: " + response.getStatusCode());
    }

    // Метод для обращения к бд, найти юзера по uid
    public UserDTO getUserByUidFirebase(String uidFirebase) {
        String databaseUrl = "http://localhost:8083/users/query?uidFirebase=" + uidFirebase;

        ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                databaseUrl, UserDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to get user by ID: " + uidFirebase);
    }

//    // Метод обновления настроек пользователя
//    public UserDTO updateUser(Long id, UserUpdateRequestDTO userId) {
//        String databaseUrl = "http://localhost:8083/users/" + userId;
//
//        ResponseEntity<UserDTO> response = restTemplate.exchange(
//                databaseUrl, HttpMethod.PUT, new HttpEntity<>(userId), UserDTO.class
//        );
//
//        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//            return response.getBody();
//        }
//        throw new RuntimeException("Failed to update user. Response code: " + response.getStatusCode());
//    }

    // Метод удаления пользователя
    public void deleteUser(String uidFirebase) {
        String databaseUrl = "http://localhost:8083/users/uid/" + uidFirebase;
        restTemplate.delete(databaseUrl);
    }
}
