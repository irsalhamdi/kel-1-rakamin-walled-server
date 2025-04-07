package com.odp.walled.repository;

import com.odp.walled.model.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
                SELECT t FROM Transaction t
                WHERE (:type IS NULL OR t.transactionType = :type)
                  AND (:startDate IS NULL OR t.transactionDate >= :startDate)
                  AND (:endDate IS NULL OR t.transactionDate <= :endDate)
                  AND (:walletId IS NULL OR t.wallet.id = :walletId OR t.recipientWallet.id = :walletId)
            """)
    Page<Transaction> findFilteredTransactions(
            @Param("type") com.odp.walled.model.Transaction.TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("walletId") Long walletId,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId OR t.recipientWallet.id = :walletId")
    List<Transaction> findAllByWalletIdOrRecipientWalletId(@Param("walletId") Long walletId);

    @Query("""
                SELECT COALESCE(SUM(t.amount), 0)
                FROM Transaction t
                WHERE
                    (t.transactionType = 'TOP_UP' AND t.wallet.id = :walletId)
                    OR
                    (t.transactionType = 'TRANSFER' AND t.recipientWallet.id = :walletId)
            """)
    BigDecimal getTotalIncome(@Param("walletId") Long walletId);


    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'TRANSFER' AND t.wallet.id = :walletId")
    BigDecimal getTotalOutcome(@Param("walletId") Long walletId);

    @Query("""
                        SELECT COALESCE(SUM(t.amount), 0)
                        FROM Transaction t
                        WHERE
                            (
                                (t.transactionType = 'TOP_UP' AND t.wallet.id = :walletId)
                                OR
                                (t.transactionType = 'TRANSFER' AND t.recipientWallet.id = :walletId)
                            )
                            AND t.transactionDate BETWEEN :start AND :end
                    """)
    BigDecimal sumIncome(Long walletId, LocalDateTime start, LocalDateTime end);

    @Query("""
                        SELECT COALESCE(SUM(t.amount), 0)
                        FROM Transaction t
                        WHERE
                            t.transactionType = 'TRANSFER'
                            AND t.wallet.id = :walletId
                            AND t.transactionDate BETWEEN :start AND :end
                    """)
    BigDecimal sumOutcome(Long walletId, LocalDateTime start, LocalDateTime end);
}