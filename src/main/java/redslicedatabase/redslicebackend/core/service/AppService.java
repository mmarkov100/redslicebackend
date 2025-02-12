package redslicedatabase.redslicebackend.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AppService {

    private final RestTemplate restTemplate;

    @Autowired
    public AppService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Boolean> servicesEnabled(){
        Map<String, Boolean> servicesEnabled = new HashMap<>();

        servicesEnabled.put("back", true);
        servicesEnabled.put("database", checkDatabaseService(restTemplate));
        servicesEnabled.put("generator", checkGeneratorService(restTemplate));

        return servicesEnabled;
    }

    // Метод для проверки работы генератора сообщений
    public static boolean checkGeneratorService(RestTemplate restTemplate){
        String generatorUrl = "http://localhost:8085/app/health";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(generatorUrl, Map.class);
            return response.getStatusCode() == HttpStatus.OK && "UP".equals(Objects.requireNonNull(response.getBody()).get("status"));
        } catch (Exception e) {
            return false;
        }
    }

    // Метод для проверки работы базы данных сервиса
    public static boolean checkDatabaseService(RestTemplate restTemplate){
        String generatorUrl = "http://localhost:8083/app/health";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(generatorUrl, Map.class);
            return response.getStatusCode() == HttpStatus.OK && "UP".equals(Objects.requireNonNull(response.getBody()).get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}
