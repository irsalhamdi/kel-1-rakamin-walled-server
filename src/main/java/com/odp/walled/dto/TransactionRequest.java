package com.odp.walled.dto;

import com.odp.walled.model.Transaction.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotNull
    private Long walletId;

    @NotNull
    private TransactionType transactionType;

    @DecimalMin("0.01") @NotNull
    @DecimalMin(value = "10000.00", message = "Minimum transaction is IDR 10,000")
    @DecimalMax(value = "2000000.00", message = "Maximum transaction is IDR 2,000,000")
    private BigDecimal amount;

    private String recipientAccountNumber;

    private String description;

    private String option;
}