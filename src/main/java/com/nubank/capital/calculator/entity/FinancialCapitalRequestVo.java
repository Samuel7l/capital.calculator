package com.nubank.capital.calculator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialCapitalRequestVo {

    String operation;
    BigDecimal unitCost;
    Integer quantity;
}
