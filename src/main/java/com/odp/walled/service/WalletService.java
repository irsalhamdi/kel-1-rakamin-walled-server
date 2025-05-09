package com.odp.walled.service;

import com.odp.walled.dto.WalletResponse;
import com.odp.walled.exception.DuplicateException;
import com.odp.walled.exception.ResourceNotFound;
import com.odp.walled.mapper.WalletMapper;
import com.odp.walled.model.User;
import com.odp.walled.model.Wallet;
import com.odp.walled.repository.UserRepository;
import com.odp.walled.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;

    public WalletResponse createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Optional<Wallet> existingWallet = walletRepository.findByUser(user);
        if (existingWallet.isPresent()) {
            throw new DuplicateException("User already has a wallet");
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setAccountNumber(generateUniqueAccountNumber());
        wallet.setBalance(BigDecimal.ZERO);
        return walletMapper.toResponse(walletRepository.save(wallet));
    }

    public WalletResponse getWalletById(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Wallet not found"));
        return walletMapper.toResponse(wallet);
    }

    public List<WalletResponse> getWalletsByUserId(Long userId) {
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        return walletMapper.toResponseList(wallets);

    }

    public List<WalletResponse> getAllWallets() {
        return walletMapper.toResponseList(walletRepository.findAll());
    }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.format("%020d", Math.abs(random.nextLong())).substring(0, 12);
        } while (walletRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

}