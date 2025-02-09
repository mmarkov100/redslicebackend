package redslicedatabase.redslicebackend.features.branch.controller;

/*
Контроллер для веток
 */

import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redslicedatabase.redslicebackend.features.branch.dto.inbound.BranchCreateRequestDTO;
import redslicedatabase.redslicebackend.features.branch.dto.outbound.BranchDTO;
import redslicedatabase.redslicebackend.features.branch.repository.BranchRepository;
import redslicedatabase.redslicebackend.features.authfirebase.service.AccountCheckService;

import java.util.List;

@RestController
@RequestMapping("/branches")
public class BranchController {

    private static final Logger logger = LoggerFactory.getLogger(BranchController.class);

    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private AccountCheckService accountCheckService;

    // Метод создания новой ветки
    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(@RequestHeader String JWTFirebase,
                                                  @RequestBody BranchCreateRequestDTO branchRequest) throws FirebaseAuthException {
        logger.info("POST: branch, ChatID: {}", branchRequest.getChatId());
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("POST branch: User uidFirebase: {}", uidFirebase);
        BranchDTO newBranch = branchRepository.createBranch(branchRequest, uidFirebase);
        logger.info("POST branch: Branch created, UID: {}, BranchID: {}", uidFirebase, newBranch.getId());
        return ResponseEntity.ok(newBranch);
    }

    // Метод получения информации всех веток чата по ID
    @PostMapping("/chat/{chatId}")
    public ResponseEntity<List<BranchDTO>> getBranchesByChatId(@PathVariable Long chatId,
                                                               @RequestHeader String JWTFirebase) throws FirebaseAuthException {
        logger.info("GET: branches, ChatID: {}", chatId);
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("GET branches: User uidFirebase: {}", uidFirebase);
        List<BranchDTO> branches = branchRepository.getBranchesByChatId(chatId, uidFirebase);
        logger.info("GET branches: Branches got, UID: {}, ChatID: {}", uidFirebase, chatId);
        return ResponseEntity.ok(branches);
    }

    // Удаление конкретной ветки чата
    @DeleteMapping("/{branchId}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long branchId,
                                             @RequestHeader String JWTFirebase) throws FirebaseAuthException {
        logger.info("DELETE: branch, branchId: {}", branchId);
        String uidFirebase = accountCheckService.getUidFirebase(JWTFirebase); // Получаем uid Пользователя
        logger.info("DELETE branch: User uidFirebase: {}", uidFirebase);
        branchRepository.deleteBranch(branchId, uidFirebase);
        logger.info("DELETE branch: Branch deleted, UID: {}", uidFirebase);
        return ResponseEntity.noContent().build();
    }

}
