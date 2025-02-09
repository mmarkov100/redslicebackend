package redslicedatabase.redslicebackend.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppController {

    private static Logger logger = LoggerFactory.getLogger(AppController.class);

    @PostMapping("/health")
    public ResponseEntity<?> health () {
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/isenabled")
    public ResponseEntity<?> isEnabled() {
        return ResponseEntity.ok().body("OK");
    }
}
