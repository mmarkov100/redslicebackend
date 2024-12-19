package redslicedatabase.redslicebackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.dto.User.inbound.UserCreateRequestDTO;
import redslicedatabase.redslicebackend.dto.User.outbound.UserDTO;

@Repository
public class UserRepository {

    private final RestTemplate restTemplate;

    @Autowired
    public UserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO createUser(UserCreateRequestDTO userRequest) {
        String databaseUrl = "http://localhost:8083/users";

        ResponseEntity<UserDTO> response = restTemplate.postForEntity(
                databaseUrl, userRequest, UserDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to create user. Response code: " + response.getStatusCode());
    }

}
