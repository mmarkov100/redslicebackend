package redslicedatabase.redslicebackend.features.genapi.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@Getter
public class GenApiConfig {

    private final String genApiUrl = "http://localhost:8085/genapi/generate";
}
