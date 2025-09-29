package com.wallet.service;

import com.wallet.entity.OperationType;
import com.wallet.entity.Wallet;
import com.wallet.exception.InsufficientFundsException;
import com.wallet.exception.WalletNotFoundException;
import com.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    
    @Mock
    private WalletRepository walletRepository;
    
    @InjectMocks
    private WalletService walletService;
    
    @Test
    void getBalance_walletExists_returnsBalance() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(new BigDecimal("100.00"));
        
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        
        BigDecimal balance = walletService.getBalance(walletId);
        
        assertEquals(new BigDecimal("100.00"), balance);
    }
    
    @Test
    void getBalance_walletNotFound_throwsException() {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());
        
        assertThrows(WalletNotFoundException.class, () -> walletService.getBalance(walletId));
    }
    
    @Test
    void processOperation_deposit_validAmount_updatesBalance() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(new BigDecimal("100.00"));
        
        when(walletRepository.findByIdWithLock(walletId)).thenReturn(Optional.of(wallet));
        
        walletService.processOperation(walletId, OperationType.DEPOSIT, new BigDecimal("50.00"));
        
        verify(walletRepository).save(wallet);
        assertEquals(new BigDecimal("150.00"), wallet.getBalance());
    }
    
    @Test
    void processOperation_withdraw_validAmount_updatesBalance() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(new BigDecimal("100.00"));
        
        when(walletRepository.findByIdWithLock(walletId)).thenReturn(Optional.of(wallet));
        
        walletService.processOperation(walletId, OperationType.WITHDRAW, new BigDecimal("30.00"));
        
        verify(walletRepository).save(wallet);
        assertEquals(new BigDecimal("70.00"), wallet.getBalance());
    }
    
    @Test
    void processOperation_withdraw_insufficientFunds_throwsException() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(new BigDecimal("20.00"));
        
        when(walletRepository.findByIdWithLock(walletId)).thenReturn(Optional.of(wallet));
        
        assertThrows(InsufficientFundsException.class, () -> 
            walletService.processOperation(walletId, OperationType.WITHDRAW, new BigDecimal("50.00")));
        
        verify(walletRepository, never()).save(any());
    }
}
