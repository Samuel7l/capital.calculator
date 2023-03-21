package com.nubank.capital.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubank.capital.calculator.entity.FinancialCapitalRequestVo;
import com.nubank.capital.calculator.entity.FinancialCapitalResponseVo;
import com.nubank.capital.calculator.service.FinancialCapitalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialCapitalControllerTest {

    @InjectMocks
    FinancialCapitalController financialCapitalController;

    @Mock
    FinancialCapitalService financialCapitalService;

    @Test
    void shouldPassOnTheConditionalTestCaseAndReturnOkStatus() throws IOException {

        String fileName = "case1.json";
        final Path requestFile = Path.of("src/test/resources/wiremock/input/").resolve(fileName);
        String requestJson = Files.readString(requestFile);
        final Path responseFile = Path.of("src/test/resources/wiremock/output/").resolve(fileName);
        String responseJson = Files.readString(responseFile);

        ObjectMapper mapper = new ObjectMapper();

        List<FinancialCapitalRequestVo> financialCapitalRequestVoList = mapper.readValue(requestJson,
                mapper.getTypeFactory().constructCollectionType(List.class, FinancialCapitalRequestVo.class));

        List<FinancialCapitalResponseVo> expectedFinancialCapitalResponseVoList = mapper.readValue(responseJson,
                mapper.getTypeFactory().constructCollectionType(List.class, FinancialCapitalResponseVo.class));

        when(financialCapitalService.calculateFinancialCapital(financialCapitalRequestVoList))
                .thenReturn(expectedFinancialCapitalResponseVoList);

        ResponseEntity<List<FinancialCapitalResponseVo>> actualFinancialCapitalResponseVoList =
                financialCapitalController.calculateFinancialCapital(financialCapitalRequestVoList);

        Assertions.assertEquals(actualFinancialCapitalResponseVoList.getBody(),
                expectedFinancialCapitalResponseVoList);
        Assertions.assertEquals(actualFinancialCapitalResponseVoList.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void shouldPassOnTheConditionalTestCaseAndReturnNoContentStatus() {

        ResponseEntity<List<FinancialCapitalResponseVo>> actualFinancialCapitalResponseVoList =
                financialCapitalController.calculateFinancialCapital(Collections.emptyList());

        Assertions.assertNull(actualFinancialCapitalResponseVoList.getBody());
        Assertions.assertEquals(actualFinancialCapitalResponseVoList.getStatusCode(), HttpStatus.NO_CONTENT);
    }
}
