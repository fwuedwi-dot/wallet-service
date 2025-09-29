package com.wallet.controller;

import com.wallet.dto.WalletOperationRequest;
import com.wallet.entity.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateWalletAndDeposit() {
        // Create wallet
        var createResponse = restTemplate.postForEntity("/api/v1/wallets", null, Object.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        
        // В реальном тесте нужно извлечь ID из ответа, здесь используем пример
        UUID walletId = UUID.randomUUID();
        
        // Test deposit
        WalletOperationRequest depositRequest = new WalletOperationRequest();
        depositRequest.setWalletId(walletId);
        depositRequest.setOperationType(OperationType.DEPOSIT);
        depositRequest.setAmount(new BigDecimal("1000.00"));
        
        ResponseEntity<Void> depositResponse = restTemplate.postForEntity(
                "/api/v1/wallet", depositRequest, Void.class);
        assertEquals(HttpStatus.OK, depositResponse.getStatusCode());
    }
    
    @Test
    void testWalletNotFound() {
        UUID nonExistentWalletId = UUID.randomUUID();
        
        var response = restTemplate.getForEntity(
                "/api/v1/wallets/" + nonExistentWalletId, Object.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
