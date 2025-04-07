package com.odp.walled.service;

import com.odp.walled.dto.BalanceGraphRequest;
import com.odp.walled.dto.BalanceGraphResponse;
import com.odp.walled.dto.BalanceGraphResult;
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

import java.util.Comparator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
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

        BigDecimal amount = request.getAmount();
        BigDecimal minAmount = BigDecimal.valueOf(10_000);
        BigDecimal maxAmount = BigDecimal.valueOf(2_000_000);

        if (amount.compareTo(minAmount) < 0 || amount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException("Transaction amount must be between Rp10,000 - Rp2,000,000");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setOption(request.getOption());

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

    public BalanceGraphResult getGraph(BalanceGraphRequest request) {
        List<BalanceGraphResponse> rawData;

        switch (request.getView().toLowerCase()) {
            case "weekly" -> rawData = getWeeklyGraph(request);
            case "monthly" -> rawData = getMonthlyGraph(request);
            case "quartal" -> rawData = getQuarterlyGraph(request);
            default -> throw new IllegalArgumentException("Unsupported view type");
        }

        // Cari nilai maksimum untuk persentase
        BigDecimal max = rawData.stream()
                .flatMap(d -> List.of(d.getIncome(), d.getOutcome()).stream())
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        // Hitung persen
        for (BalanceGraphResponse d : rawData) {
            d.setIncomePercent(max.compareTo(BigDecimal.ZERO) == 0 ? 0
                    : d.getIncome().divide(max, 4, RoundingMode.HALF_UP).doubleValue() * 100);
            d.setOutcomePercent(max.compareTo(BigDecimal.ZERO) == 0 ? 0
                    : d.getOutcome().divide(max, 4, RoundingMode.HALF_UP).doubleValue() * 100);
        }

        return new BalanceGraphResult(
                request.getWalletId(),
                request.getView(),
                request.getYear(),
                request.getView().equalsIgnoreCase("weekly") ? request.getMonth() : null,
                max,
                rawData);
    }

    private List<BalanceGraphResponse> getWeeklyGraph(BalanceGraphRequest request) {
        List<BalanceGraphResponse> result = new ArrayList<>();
        int year = request.getYear();
        int monthValue = Integer.parseInt(request.getMonth());
        LocalDate firstDay = LocalDate.of(year, monthValue, 1);
        int lastDay = firstDay.lengthOfMonth();

        int[] startDays = { 1, 9, 17, 25 };
        int[] endDays = { 8, 16, 24, lastDay };

        for (int i = 0; i < 4; i++) {
            LocalDate start = LocalDate.of(year, monthValue, startDays[i]);
            LocalDate end = LocalDate.of(year, monthValue, endDays[i]);

            BigDecimal income = transactionRepository.sumIncome(request.getWalletId(), start.atStartOfDay(),
                    end.atTime(23, 59));
            BigDecimal outcome = transactionRepository.sumOutcome(request.getWalletId(), start.atStartOfDay(),
                    end.atTime(23, 59));

            result.add(new BalanceGraphResponse(startDays[i] + " - " + endDays[i], income, outcome, 0, 0));
        }
        return result;
    }

    private List<BalanceGraphResponse> getMonthlyGraph(BalanceGraphRequest request) {
        List<BalanceGraphResponse> result = new ArrayList<>();
        int year = request.getYear();

        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal income = transactionRepository.sumIncome(request.getWalletId(), start.atStartOfDay(),
                    end.atTime(23, 59));
            BigDecimal outcome = transactionRepository.sumOutcome(request.getWalletId(), start.atStartOfDay(),
                    end.atTime(23, 59));

            result.add(new BalanceGraphResponse(ym.getMonth().toString().substring(0, 3), income, outcome, 0, 0));
        }
        return result;
    }

    private List<BalanceGraphResponse> getQuarterlyGraph(BalanceGraphRequest request) {
        List<BalanceGraphResponse> result = new ArrayList<>();
        int year = request.getYear();
        String[] labels = { "Jan - Mar", "Apr - Jun", "Jul - Sep", "Oct - Dec" };

        int[][] ranges = {
                { 1, 3 },
                { 4, 6 },
                { 7, 9 },
                { 10, 12 }
        };

        for (int i = 0; i < 4; i++) {
            LocalDate start = LocalDate.of(year, ranges[i][0], 1);
            LocalDate end = YearMonth.of(year, ranges[i][1]).atEndOfMonth();

            BigDecimal income = transactionRepository.sumIncome(request.getWalletId(), start.atStartOfDay(),
                    end.atTime(23, 59));
            BigDecimal outcome = transactionRepository.sumOutcome(request.getWalletId(), start.atStartOfDay(),
                    end.atTime(23, 59));

            result.add(new BalanceGraphResponse(labels[i], income, outcome, 0, 0));
        }
        return result;
    }
}