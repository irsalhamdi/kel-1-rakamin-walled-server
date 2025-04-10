package com.odp.walled.controller;

import com.odp.walled.dto.APIResponse;
import com.odp.walled.dto.BalanceGraphRequest;
import com.odp.walled.dto.BalanceGraphResult;
import com.odp.walled.dto.TransactionRequest;
import com.odp.walled.dto.TransactionResponse;
import com.odp.walled.dto.WalletSummaryDTO;
import com.odp.walled.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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
    public ResponseEntity<APIResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse data = transactionService.processTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>("success", "Transaction processed successfully", data));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<TransactionResponse>>> getTransactionsByWallet(
            @RequestParam Long walletId) {
        List<TransactionResponse> data = transactionService.getTransactionsByWallet(walletId);
        return ResponseEntity.ok(
                new APIResponse<>("success", "Transactions retrieved successfully", data));
    }


    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<TransactionResponse>> getTransactionByID(@PathVariable Long id) {
        TransactionResponse data = transactionService.getTransactionByID(id);
        return ResponseEntity.ok(new APIResponse<>("success", "Transaction retrieved successfully", data));
    }


    @GetMapping("/summary/{walletId}")
    public ResponseEntity<APIResponse<WalletSummaryDTO>> getWalletSummary(@PathVariable Long walletId) {
        WalletSummaryDTO data = transactionService.getWalletSummary(walletId);
        return ResponseEntity.ok(new APIResponse<>("success", "Wallet summary retrieved successfully", data));
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

    @PostMapping("/graph")
    public BalanceGraphResult getGraph(@RequestBody BalanceGraphRequest request) {
        return transactionService.getGraph(request);
    }

    @GetMapping("/export-pdf/{id}")
    public ResponseEntity<byte[]> exportTransactionPdf(@PathVariable Long id) {
        byte[] pdfBytes = transactionService.generateTransactionPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}