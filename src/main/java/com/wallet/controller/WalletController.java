package com.wallet.controller;

import com.wallet.dto.WalletBalanceResponse;
import com.wallet.dto.WalletOperationRequest;
import com.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {
    
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
    private final WalletService walletService;
    
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    
    @PostMapping("/wallet")
    public ResponseEntity<Void> processWalletOperation(@Valid @RequestBody WalletOperationRequest request) {
        logger.info("Processing {} for wallet {} amount {}", 
                   request.getOperationType(), request.getWalletId(), request.getAmount());
        
        walletService.processOperation(request.getWalletId(), request.getOperationType(), request.getAmount());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<WalletBalanceResponse> getWalletBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(new WalletBalanceResponse(walletId, balance));
    }
    
    @PostMapping("/wallets")
    public ResponseEntity<WalletBalanceResponse> createWallet() {
        var wallet = walletService.createWallet();
        return ResponseEntity.ok(new WalletBalanceResponse(wallet.getId(), wallet.getBalance()));
    }
}
