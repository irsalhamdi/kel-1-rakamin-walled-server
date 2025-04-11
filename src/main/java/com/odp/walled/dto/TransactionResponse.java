package com.odp.walled.dto;

import com.odp.walled.model.Transaction;
import com.odp.walled.model.Transaction.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long walletId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Long recipientWalletId;
    private LocalDateTime transactionDate;
    private String description;
    private String option;
    private String senderName;
    private String recipientName;

    public static TransactionResponse fromEntity(Transaction tx) {
        TransactionResponse res = new TransactionResponse();
        res.setId(tx.getId());
        res.setWalletId(tx.getWallet().getId());
        res.setTransactionType(tx.getTransactionType());
        res.setAmount(tx.getAmount());
        res.setRecipientWalletId(
                tx.getRecipientWallet() != null ? tx.getRecipientWallet().getId() : null);
        res.setTransactionDate(tx.getTransactionDate());
        res.setDescription(tx.getDescription());
        res.setOption(tx.getOption());

        // NEW DATA
        res.setSenderName(tx.getWallet().getUser().getFullname());
        if (tx.getRecipientWallet() != null && tx.getRecipientWallet().getUser() != null) {
            res.setRecipientName(tx.getRecipientWallet().getUser().getFullname());
        }

        return res;
    }
}