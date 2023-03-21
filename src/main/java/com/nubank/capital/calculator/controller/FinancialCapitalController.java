package com.nubank.capital.calculator.controller;

import com.nubank.capital.calculator.entity.FinancialCapitalRequestVo;
import com.nubank.capital.calculator.entity.FinancialCapitalResponseVo;
import com.nubank.capital.calculator.service.FinancialCapitalService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financialCapital")
public class FinancialCapitalController {

    @Autowired
    private final FinancialCapitalService financialCapitalService;

    public FinancialCapitalController (FinancialCapitalService financialCapitalService) {
        this.financialCapitalService = financialCapitalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<FinancialCapitalResponseVo>> calculateFinancialCapital
            (@RequestBody List<FinancialCapitalRequestVo> financialCapitalRequestVo) {

        if (CollectionUtils.isEmpty(financialCapitalRequestVo)) {
            return ResponseEntity.noContent().build();
        }

        List<FinancialCapitalResponseVo> financialCapitalResponseVoList = financialCapitalService.
                calculateFinancialCapital(financialCapitalRequestVo);

        return ResponseEntity.ok(financialCapitalResponseVoList);
    }

}
