package redslicedatabase.redslicebackend.features.genapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.core.config.ApiConfig;
import redslicedatabase.redslicebackend.features.genapi.config.GenApiConfig;
import redslicedatabase.redslicebackend.features.genapi.dto.inbound.MessageGeneratorResponseGenApiDTO;
import redslicedatabase.redslicebackend.features.genapi.dto.outbound.GenApiRequest;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateDTO;

@Service
public class DeepSeekV3Service implements GenApiService{

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekV3Service.class);

    @Autowired
    private GenApiConfig genApiConfig;
    @Autowired
    private ApiConfig apiConfig;

    private final RestTemplate restTemplate;

    public DeepSeekV3Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Метод для получения ответа на генерацию от сервиса генератора
    @Override
    public MessageGeneratorResponseGenApiDTO generateResponse(GenApiRequest genApiRequest) throws Exception {
        String url = genApiConfig.getGenApiUrl(); //

        HttpHeaders headers = new HttpHeaders();
        headers.set("apiGeneratorKey", apiConfig.getApiDatabaseKey());

        HttpEntity<GenApiRequest> entity = new HttpEntity<>(genApiRequest, headers);

        try {
            ResponseEntity<MessageGeneratorResponseGenApiDTO> response = restTemplate.exchange(url, HttpMethod.POST, entity, MessageGeneratorResponseGenApiDTO.class);
            logger.info("Response ID: {}", response);

            return response.getBody();
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }

    @Override
    public GenApiRequest genApiRequestConvertDTO(MessageGenerateDTO messageGenerateDTO) {
        return null;
    }
}
