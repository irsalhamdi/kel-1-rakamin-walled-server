package com.odp.walled.dto;

import lombok.Data;

@Data
public class BalanceGraphRequest {
    private String view; // "weekly", "monthly", "quartal"
    private String month; // required for weekly
    private int year;
    private Long walletId;
}
