package redslicedatabase.redslicebackend.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redslicedatabase.redslicebackend.core.service.AppService;

import java.util.Map;

@RestController
@RequestMapping("/app")
public class AppController {

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/health")
    public ResponseEntity<?> health () {
        Map<String, Boolean> enables = appService.servicesEnabled();

        return ResponseEntity.ok(enables);
    }

    @PostMapping("/isenabled")
    public ResponseEntity<?> isEnabled() {


        return ResponseEntity.ok().body("OK");
    }
}
