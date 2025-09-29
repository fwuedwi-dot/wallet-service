package com.wallet.service;

import com.wallet.entity.OperationType;
import com.wallet.entity.Wallet;
import com.wallet.exception.InsufficientFundsException;
import com.wallet.exception.WalletNotFoundException;
import com.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {
    
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);
    private final WalletRepository walletRepository;
    
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }
    
    @Transactional
    public void processOperation(UUID walletId, OperationType operationType, BigDecimal amount) {
        // Используем пессимистическую блокировку
        Wallet wallet = walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        
        BigDecimal newBalance;
        if (operationType == OperationType.DEPOSIT) {
            newBalance = wallet.getBalance().add(amount);
        } else {
            // WITHDRAW
            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(walletId, wallet.getBalance(), amount);
            }
            newBalance = wallet.getBalance().subtract(amount);
        }
        
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        logger.info("Processed {} for wallet {}: amount {}, new balance {}", 
                   operationType, walletId, amount, newBalance);
    }
    
    @Transactional
    public Wallet createWallet() {
        Wallet wallet = new Wallet();
        return walletRepository.save(wallet);
    }
}
