package com.odp.walled.controller;

import com.odp.walled.dto.APIResponse;
import com.odp.walled.dto.WalletResponse;
import com.odp.walled.service.WalletService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/{userId}")
    public ResponseEntity<APIResponse<WalletResponse>> createWallet(@PathVariable Long userId) {
        WalletResponse data = walletService.createWallet(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>("success", "Wallet created successfully", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<WalletResponse>> getWalletById(@PathVariable Long id) {
        WalletResponse data = walletService.getWalletById(id);
        return ResponseEntity.ok(new APIResponse<>("success", "Wallet retrieved successfully", data));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<APIResponse<List<WalletResponse>>> getWalletsByUserId(@PathVariable Long userId) {
        List<WalletResponse> data = walletService.getWalletsByUserId(userId);
        return ResponseEntity.ok(new APIResponse<>("success", "Wallets for user retrieved", data));
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<WalletResponse>>> getAllWallets() {
        List<WalletResponse> data = walletService.getAllWallets();
        return ResponseEntity.ok(new APIResponse<>("success", "All wallets retrieved", data));
    }

}