package com.nubank.capital.calculator.service;

import com.nubank.capital.calculator.entity.FinancialCapitalRequestVo;
import com.nubank.capital.calculator.entity.FinancialCapitalResponseVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinancialCapitalService {

    private final BigDecimal TAX_RESTRICTION = BigDecimal.valueOf(20000);
    private final BigDecimal TAX_PERCENTAGE = BigDecimal.valueOf(0.20);
    public List<FinancialCapitalResponseVo> calculateFinancialCapital (List<FinancialCapitalRequestVo>
                                                                               financialCapitalRequestVoList) {

        List<FinancialCapitalResponseVo> financialCapitalResponseVoList = new ArrayList<>();

        BigDecimal WEIGHT_AVERAGE_PRICE = BigDecimal.ZERO;
        BigDecimal PROFIT = BigDecimal.ZERO;
        BigDecimal LOSS = BigDecimal.ZERO;
        int CURRENT_NUMBER_OF_SHARES = 0;
        String BUY_OPERATION = "buy";
        String SELL_OPERATION = "sell";

        for(FinancialCapitalRequestVo financialCapitalRequestVo : financialCapitalRequestVoList) {

            Integer OPERATION_QUANTITY = financialCapitalRequestVo.getQuantity();
            BigDecimal TAX = BigDecimal.ZERO.setScale(2, RoundingMode.UP);
            BigDecimal OPERATION_UNIT_COST = financialCapitalRequestVo.getUnitCost();
            BigDecimal OPERATION_TOTAL_VALUE = BigDecimal.valueOf(OPERATION_QUANTITY)
                    .multiply(OPERATION_UNIT_COST);
            String OPERATION = financialCapitalRequestVo.getOperation();
            boolean IS_PROFIT_OPERATION = false;

            if (BUY_OPERATION.equalsIgnoreCase(OPERATION)) {

                WEIGHT_AVERAGE_PRICE = ((WEIGHT_AVERAGE_PRICE.multiply(BigDecimal.valueOf(CURRENT_NUMBER_OF_SHARES)))
                        .add(OPERATION_TOTAL_VALUE))
                        .divide(BigDecimal.valueOf(CURRENT_NUMBER_OF_SHARES + OPERATION_QUANTITY),
                                RoundingMode.UP);

                CURRENT_NUMBER_OF_SHARES += OPERATION_QUANTITY;

            } else if (SELL_OPERATION.equalsIgnoreCase(OPERATION)) {

                if (OPERATION_UNIT_COST.compareTo(WEIGHT_AVERAGE_PRICE) > 0) {
                    PROFIT = PROFIT.add(calculateProfitOrLoss(WEIGHT_AVERAGE_PRICE, OPERATION_UNIT_COST, OPERATION_QUANTITY));
                    IS_PROFIT_OPERATION = true;
                } else if (OPERATION_UNIT_COST.compareTo(WEIGHT_AVERAGE_PRICE) < 0) {
                    LOSS = LOSS.add(calculateProfitOrLoss(WEIGHT_AVERAGE_PRICE, OPERATION_UNIT_COST, OPERATION_QUANTITY));
                }

                if (PROFIT.compareTo(BigDecimal.ZERO) != 0  && LOSS.compareTo(BigDecimal.ZERO) != 0) {
                    Map<String, BigDecimal> deductProfitAndLoss = deductProfitAndLoss(PROFIT, LOSS);
                    PROFIT = deductProfitAndLoss.get("PROFIT");
                    LOSS = deductProfitAndLoss.get("LOSS");
                }

                if (IS_PROFIT_OPERATION && OPERATION_TOTAL_VALUE.compareTo(TAX_RESTRICTION) > 0) {
                    TAX = PROFIT.multiply(TAX_PERCENTAGE).setScale(2, RoundingMode.UP);
                }

                CURRENT_NUMBER_OF_SHARES -= OPERATION_QUANTITY;
                if (CURRENT_NUMBER_OF_SHARES == 0) {
                    PROFIT = BigDecimal.ZERO;
                }
            }

            FinancialCapitalResponseVo financialCapitalResponseVo = new FinancialCapitalResponseVo(TAX);
            financialCapitalResponseVoList.add(financialCapitalResponseVo);
        }

        return financialCapitalResponseVoList;
    }

    private static BigDecimal calculateProfitOrLoss(BigDecimal weightedAveragePrice, BigDecimal unitCost,
                                                    Integer quantity) {
        return (weightedAveragePrice.subtract(unitCost)).multiply(BigDecimal.valueOf(quantity)).abs();
    }

    private static Map<String,BigDecimal> deductProfitAndLoss(BigDecimal profit, BigDecimal loss) {
        if (loss.compareTo(profit) >= 0) {
            loss = loss.subtract(profit);
            profit = BigDecimal.ZERO;
        } else {
            profit = profit.subtract(loss);
            loss = BigDecimal.ZERO;
        }
        Map<String,BigDecimal> mapWithProfitAndLoss = new HashMap<>();
        mapWithProfitAndLoss.put("PROFIT", profit);
        mapWithProfitAndLoss.put("LOSS", loss);
        return mapWithProfitAndLoss;
    }
}
