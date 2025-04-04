package com.odp.walled.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceGraphResult {
    private Long walletId;
    private String view;
    private int year;
    private String month;
    private BigDecimal maxValue;
    private List<BalanceGraphResponse> data;
}
