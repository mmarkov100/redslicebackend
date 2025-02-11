package redslicedatabase.redslicebackend.features.branch.repository;

/*
Репозиторий для веток
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import redslicedatabase.redslicebackend.core.config.ApiConfig;
import redslicedatabase.redslicebackend.features.branch.dto.inbound.BranchCreateRequestDTO;
import redslicedatabase.redslicebackend.features.branch.dto.outbound.BranchCreateDatabaseDTO;
import redslicedatabase.redslicebackend.features.branch.dto.outbound.BranchDTO;

import java.util.List;

@Repository
public class BranchRepository {

    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;

    @Autowired
    public BranchRepository(RestTemplate restTemplate, ApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }

    // Метод репозитория для создания ветки
    public BranchDTO createBranch(BranchCreateRequestDTO branchRequest, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/branches";

        BranchCreateDatabaseDTO branchCreateDatabaseDTO = new BranchCreateDatabaseDTO();
        branchCreateDatabaseDTO.setUidFirebase(uidFirebase);
        branchCreateDatabaseDTO.setChatId(branchRequest.getChatId());
        branchCreateDatabaseDTO.setParentBranchId(branchRequest.getParentBranchId());
        branchCreateDatabaseDTO.setMessageStartId(branchRequest.getMessageStartId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<BranchCreateDatabaseDTO> entity = new HttpEntity<>(branchCreateDatabaseDTO, headers);

        ResponseEntity<BranchDTO> response = restTemplate.postForEntity(
                databaseUrl, entity, BranchDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to create branch. Response code: " + response.getStatusCode());
    }

    // Метод репозитория для просмотра веток чата
    public List<BranchDTO> getBranchesByChatId(Long chatId, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/branches/chat/" + chatId + "/validate?uidFirebase=" + uidFirebase;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers); // Пустое тело для GET-запроса

        ResponseEntity<BranchDTO[]> response = restTemplate.exchange(
                databaseUrl, org.springframework.http.HttpMethod.GET, entity, BranchDTO[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return List.of(response.getBody());
        }
        throw new RuntimeException("Failed to get branches for chat ID: " + chatId);
    }

    // Метод репозитория для удаления конкретной ветки
    public void deleteBranch(Long branchId, String uidFirebase) {
        String databaseUrl = "http://localhost:8083/branches/" + branchId + "/validate?uidFirebase=" + uidFirebase;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apiDBKey", apiConfig.getApiDatabaseKey());
        HttpEntity<Void> entity = new HttpEntity<>(headers); // Пустое тело для DELETE-запроса

        restTemplate.exchange(databaseUrl, org.springframework.http.HttpMethod.DELETE, entity, Void.class);
    }
}
