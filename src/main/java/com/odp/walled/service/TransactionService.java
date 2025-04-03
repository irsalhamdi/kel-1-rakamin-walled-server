package com.odp.walled.service;

import com.odp.walled.dto.TransactionRequest;
import com.odp.walled.dto.TransactionResponse;
import com.odp.walled.dto.WalletSummaryDTO;
import com.odp.walled.exception.InsufficientBalanceException;
import com.odp.walled.exception.ResourceNotFound;
import com.odp.walled.mapper.TransactionMapper;
import com.odp.walled.model.Transaction;
import com.odp.walled.model.Transaction.TransactionType;
import com.odp.walled.model.Wallet;
import com.odp.walled.repository.TransactionRepository;
import com.odp.walled.repository.WalletRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new ResourceNotFound("Wallet not found"));

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());

        if (request.getTransactionType() == TransactionType.TRANSFER) {
            Wallet recipient = walletRepository.findByAccountNumber(request.getRecipientAccountNumber())
                    .orElseThrow(() -> new ResourceNotFound("Recipient wallet not found"));

            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }

            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
            recipient.setBalance(recipient.getBalance().add(request.getAmount()));
            walletRepository.save(recipient);

            // Set the recipient wallet
            transaction.setRecipientWallet(recipient);
        } else {
            // TOP_UP
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        }

        walletRepository.save(wallet);
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getTransactionsByWallet(Long walletId) {
        List<Transaction> transactions = transactionRepository
                .findAllByWalletIdOrRecipientWalletId(walletId);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    public TransactionResponse getTransactionByID(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Transaction not found"));
        return transactionMapper.toResponse(transaction);
    }

    public WalletSummaryDTO getWalletSummary(Long walletId) {
        BigDecimal totalIncome = transactionRepository.getTotalIncome(walletId);
        BigDecimal totalOutcome = transactionRepository.getTotalOutcome(walletId);
        BigDecimal balance = walletRepository.findById(walletId)
                .map(Wallet::getBalance)
                .orElse(BigDecimal.ZERO);

        return new WalletSummaryDTO(totalIncome, totalOutcome, balance);
    }

    public Page<TransactionResponse> getTransactionHistory(
            String typeStr,
            String timeRange,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long walletId,
            int page,
            int size,
            String sortBy,
            String order) {

        if (timeRange != null && !timeRange.isBlank()) {
            LocalDateTime now = LocalDateTime.now();
            switch (timeRange.toUpperCase()) {
                case "TODAY" -> {
                    startDate = now.toLocalDate().atStartOfDay();
                    endDate = now.toLocalDate().atTime(23, 59, 59);
                }
                case "YESTERDAY" -> {
                    LocalDateTime yesterday = now.minusDays(1);
                    startDate = yesterday.toLocalDate().atStartOfDay();
                    endDate = yesterday.toLocalDate().atTime(23, 59, 59);
                }
                case "THIS_WEEK" -> {
                    LocalDate today = now.toLocalDate();
                    startDate = today.with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                    endDate = today.with(java.time.DayOfWeek.SUNDAY).atTime(23, 59, 59);
                }
                case "THIS_MONTH" -> {
                    LocalDate today = now.toLocalDate();
                    startDate = today.withDayOfMonth(1).atStartOfDay();
                    endDate = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);
                }
                case "ALL_TIME" -> {
                    startDate = null;
                    endDate = null;
                }
            }
        }

        Pageable pageable = PageRequest.of(page, size,
                order.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        TransactionType type = null;
        if (typeStr != null && !typeStr.isBlank()) {
            type = TransactionType.valueOf(typeStr.toUpperCase());
        }

        return transactionRepository
                .findFilteredTransactions(type, startDate, endDate, walletId, pageable)
                .map(TransactionResponse::fromEntity);
    }

}