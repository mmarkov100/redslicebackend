package redslicedatabase.redslicebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redslicedatabase.redslicebackend.dto.User.inbound.UserCreateRequestDTO;
import redslicedatabase.redslicebackend.dto.User.outbound.UserDTO;
import redslicedatabase.redslicebackend.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateRequestDTO userRequest) {
        UserDTO newUser = userRepository.createUser(userRequest);
        return ResponseEntity.ok(newUser);
    }

}
