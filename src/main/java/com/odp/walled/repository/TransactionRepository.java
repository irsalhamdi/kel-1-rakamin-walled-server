package com.odp.walled.repository;

import com.odp.walled.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId OR t.recipientWallet.id = :walletId")
    List<Transaction> findAllByWalletIdOrRecipientWalletId(@Param("walletId") Long walletId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE (t.transactionType = 'TOP_UP' OR t.recipientWallet.id = :walletId)")
    BigDecimal getTotalIncome(@Param("walletId") Long walletId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = 'TRANSFER' AND t.wallet.id = :walletId")
    BigDecimal getTotalOutcome(@Param("walletId") Long walletId);

}