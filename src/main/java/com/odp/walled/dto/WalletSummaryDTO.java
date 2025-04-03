package com.odp.walled.dto;

import java.math.BigDecimal;

public class WalletSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalOutcome;
    private BigDecimal balance;

    public WalletSummaryDTO(BigDecimal totalIncome, BigDecimal totalOutcome, BigDecimal balance) {
        this.totalIncome = totalIncome;
        this.totalOutcome = totalOutcome;
        this.balance = balance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalOutcome() {
        return totalOutcome;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
