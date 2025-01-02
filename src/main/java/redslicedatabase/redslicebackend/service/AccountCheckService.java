package redslicedatabase.redslicebackend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class AccountCheckService {
    public String getUidFirebase(String JWTFirebase) throws FirebaseAuthException {
        // Проверка и декодирование токена Firebase JWT
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(JWTFirebase);

        // Возвращаем UID пользователя из токена
        return decodedToken.getUid();
    }
}
