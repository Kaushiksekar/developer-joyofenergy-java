package uk.tw.energy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.tw.energy.domain.Account;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.EnergySupplier;
import uk.tw.energy.generator.ElectricityReadingsGenerator;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.CostService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AccountControllerTest {

    private static final String PRICE_PLAN_1_ID = "test-supplier";
    private static final String SMART_METER_ID = "smart-meter-id";

    private AccountController accountController;

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        CostService costService = new CostService();
        EnergySupplier energySupplier1 = new EnergySupplier(null, PRICE_PLAN_1_ID, BigDecimal.TEN, null);

        final Map<String, Account> map = new HashMap<>();
        final ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        map.put(SMART_METER_ID, new Account("smart-meter-0", energySupplier1));
        map.keySet().forEach(smartMeterId -> map.get(smartMeterId).addElectricityReadings(
                electricityReadingsGenerator.generate(20)
        ));

        accountService = new AccountService(map, costService);
        accountController = new AccountController(accountService);
    }

    @Test
    public void shouldCalculateLastWeekCost() {
        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        accountService.storeReadings(SMART_METER_ID, Arrays.asList(electricityReading, otherReading));

        ResponseEntity<Map<String, Object>> response = accountController.calculateCost(SMART_METER_ID, "1w");
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void givenUnknownAccountShouldReturnNotFound() {
        ResponseEntity<Map<String, Object>> response = accountController.calculateCost(
                "unknown-smart-meter", "1w"
        );
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}
