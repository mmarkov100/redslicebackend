package redslicedatabase.redslicebackend.features.user.controller;

/*
Контроллер для сообщений
 */

import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redslicedatabase.redslicebackend.features.user.dto.inbound.UserCreateRequestDTO;
import redslicedatabase.redslicebackend.features.user.dto.outbound.UserDTO;
import redslicedatabase.redslicebackend.features.user.repository.UserRepository;
import redslicedatabase.redslicebackend.features.authfirebase.service.AccountCheckService;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountCheckService accountCheckService;

    // Эндпоинт создания нового пользователя (регистрация)
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestHeader String JWTFirebase,
                                              @RequestBody UserCreateRequestDTO userRequest) throws FirebaseAuthException {
        logger.info("POST: user");
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("POST user: User uidFirebase: {}", uidFirebase);
        UserDTO newUser = userRepository.createUser(userRequest, uidFirebase); // Делаем запрос на датабазу и получаем ответ
        logger.info("POST user: User created: {}", uidFirebase);
        return ResponseEntity.ok(newUser);
    }

    // Эндпоинт получения пользователя (вход)
    @PostMapping("/user")
    public ResponseEntity<UserDTO> getUserById(@RequestHeader String JWTFirebase) throws FirebaseAuthException {
        logger.info("GET: user");
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("GET user: User uidFirebase: {}", uidFirebase);
        UserDTO user = userRepository.getUserByUidFirebase(uidFirebase); // Отправляем запрос на датабазу
        logger.info("GET user: User got: {}", uidFirebase);
        return ResponseEntity.ok(user);
    }

//    // Эндпоинт для обновления настроек пользователя, ПОКА ЧТО НЕ НУЖЕН
////    @PutMapping
//    public ResponseEntity<UserDTO> updateUser(@RequestHeader Long id, // Вместо этого будет JWT токен авторизации, также в каждом запросе к бэку от фронта. От него возьмем id
//                                              @RequestBody UserUpdateRequestDTO userRequest) {
//        UserDTO updatedUser = userRepository.updateUser(id, userRequest);
//        return ResponseEntity.ok(updatedUser);
//    }

    // Удалить пользователя по id
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader String JWTFirebase) throws FirebaseAuthException {
        logger.info("DELETE: user");
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("DELETE user: User uidFirebase: {}", uidFirebase);
        userRepository.deleteUser(uidFirebase);
        logger.info("DELETE user: User deleted: {}", uidFirebase);
        return ResponseEntity.noContent().build();
    }
}
