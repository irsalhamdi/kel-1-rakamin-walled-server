package com.odp.walled.controller;

import com.odp.walled.dto.TransactionRequest;
import com.odp.walled.dto.TransactionResponse;
import com.odp.walled.dto.WalletSummaryDTO;
import com.odp.walled.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
        return transactionService.processTransaction(request);
    }

    @GetMapping
    public List<TransactionResponse> getTransactionsByWallet(
            @RequestParam Long walletId) {
        return transactionService.getTransactionsByWallet(walletId);
    }

    @GetMapping("/{id}")
    public TransactionResponse getTransactionByID(@PathVariable Long id) {
        return transactionService.getTransactionByID(id);
    }

    @GetMapping("/summary/{walletId}")
    public ResponseEntity<WalletSummaryDTO> getWalletSummary(@PathVariable Long walletId) {
        return ResponseEntity.ok(transactionService.getWalletSummary(walletId));
    }

    @GetMapping("/filter")
    public Page<TransactionResponse> getTransactionHistory(
            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        return transactionService.getTransactionHistory(type, timeRange, startDate, endDate, walletId, page, size,
                sortBy, order);
    }

}