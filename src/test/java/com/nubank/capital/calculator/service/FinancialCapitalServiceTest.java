package com.nubank.capital.calculator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubank.capital.calculator.entity.FinancialCapitalRequestVo;
import com.nubank.capital.calculator.entity.FinancialCapitalResponseVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FinancialCapitalServiceTest {

    @InjectMocks
    FinancialCapitalService financialCapitalService;

    @ParameterizedTest
    @ValueSource(strings = {"case1.json","case2.json","case3.json","case4.json",
            "case5.json","case6.json","case7.json","case8.json"})
    void shouldPassOnTheConditionalTestCases(String fileName) throws IOException {

        final Path requestFile = Path.of("src/test/resources/wiremock/input/").resolve(fileName);
        String requestJson = Files.readString(requestFile);
        final Path responseFile = Path.of("src/test/resources/wiremock/output/").resolve(fileName);
        String responseJson = Files.readString(responseFile);

        ObjectMapper mapper = new ObjectMapper();

        List<FinancialCapitalRequestVo> financialCapitalRequestVoList = mapper.readValue(requestJson,
                mapper.getTypeFactory().constructCollectionType(List.class, FinancialCapitalRequestVo.class));

        List<FinancialCapitalResponseVo> expectedFinancialCapitalResponseVoList = mapper.readValue(responseJson,
                mapper.getTypeFactory().constructCollectionType(List.class, FinancialCapitalResponseVo.class));

        List<FinancialCapitalResponseVo> actualFinancialCapitalResponseVoList = financialCapitalService
                .calculateFinancialCapital(financialCapitalRequestVoList);

        Assertions.assertEquals(actualFinancialCapitalResponseVoList, expectedFinancialCapitalResponseVoList);
    }

}
