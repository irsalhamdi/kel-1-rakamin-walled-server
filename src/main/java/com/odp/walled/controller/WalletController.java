package com.odp.walled.controller;

import com.odp.walled.dto.WalletResponse;
import com.odp.walled.model.Wallet;
import com.odp.walled.service.WalletService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@PathVariable Long userId) {
        return walletService.createWallet(userId);
    }

    @GetMapping("/{id}")
    public WalletResponse getWalletById(@PathVariable Long id) {
        return walletService.getWalletById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Wallet> getWalletsByUserId(@PathVariable Long userId) {
        return walletService.getWalletsByUserId(userId);
    }

    @GetMapping
    public List<Wallet> getAllWallets() {
        return walletService.getAllWallets();
    }
}