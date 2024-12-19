package redslicedatabase.redslicebackend.controller;

/*
Контроллер для веток
 */

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redslicedatabase.redslicebackend.dto.Branch.inbound.BranchCreateRequestDTO;
import redslicedatabase.redslicebackend.dto.Branch.outbound.BranchDTO;
import redslicedatabase.redslicebackend.repository.BranchRepository;

import java.util.List;

@RestController
@RequestMapping("/branches")
public class BranchController {

    private final BranchRepository branchRepository;

    public BranchController(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    // Метод создания новой ветки
    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(@RequestBody BranchCreateRequestDTO branchRequest) {
        BranchDTO newBranch = branchRepository.createBranch(branchRequest);
        return ResponseEntity.ok(newBranch);
    }

    // Метод получения веток чата по ID
    @GetMapping("/chat/{id}")
    public ResponseEntity<List<BranchDTO>> getBranchesByChatId(@PathVariable Long id) {
        List<BranchDTO> branches = branchRepository.getBranchesByChatId(id);
        return ResponseEntity.ok(branches);
    }

    // Удаление конкретной ветки чата
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchRepository.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

}
