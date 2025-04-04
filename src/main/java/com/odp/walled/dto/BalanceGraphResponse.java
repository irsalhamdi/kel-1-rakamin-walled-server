// BalanceGraphResponse.java
package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceGraphResponse {
    private String label;
    private BigDecimal income;
    private BigDecimal outcome;
    private double incomePercent;
    private double outcomePercent;
}
