package redslicedatabase.redslicebackend.features.user.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.core.config.ApiConfig;
import redslicedatabase.redslicebackend.features.user.dto.inbound.UserCreateRequestDTO;
import redslicedatabase.redslicebackend.features.user.dto.outbound.UserDTO;
import redslicedatabase.redslicebackend.features.user.dto.outbound.UserCreateDatabaseDTO;

@Repository
public class UserRepository {

    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private final ApiConfig apiConfig;

    public UserRepository(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }


    // Метод для обращения к бд создание нового пользователя
    public UserDTO createUser(UserCreateRequestDTO userRequest, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/users";

        UserCreateDatabaseDTO userCreateDatabaseDTO = new UserCreateDatabaseDTO();
        userCreateDatabaseDTO.setEmail(userRequest.getEmail());
        userCreateDatabaseDTO.setUidFirebase(uidFirebase);
        userCreateDatabaseDTO.setTotalTokens(10000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers);  // пустое тело для GET-запроса

        ResponseEntity<UserDTO> response = restTemplate.exchange(
                databaseUrl, HttpMethod.GET, entity, UserDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to create user. Response code: " + response.getStatusCode());
    }

    // Метод для обращения к бд, найти юзера по uid
    public UserDTO getUserByUidFirebase(String uidFirebase) {
        String databaseUrl = "http://localhost:8083/users/query?uidFirebase=" + uidFirebase;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<UserDTO> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                databaseUrl, UserDTO.class, entity
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());

        try {
            restTemplate.delete(databaseUrl, headers);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + uidFirebase, e);
        }
    }
}
