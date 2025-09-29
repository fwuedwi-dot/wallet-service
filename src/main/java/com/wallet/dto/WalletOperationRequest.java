package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.entity.OperationType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletOperationRequest {
    
    @NotNull(message = "walletId is required")
    @JsonProperty("valletId")
    private UUID walletId;
    
    @NotNull(message = "operationType is required")
    
    private OperationType operationType;
    
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;
    
    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }
    
    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}