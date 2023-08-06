package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.Account;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.EnergySupplier;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AccountServiceTest {

    private static final String PRICE_PLAN_1_ID = "test-supplier";
    private static final String SMART_METER_ID = "smart-meter-id";

    private AccountService accountService;

    private Account account;

    List<ElectricityReading> electricityReadings;

    @BeforeEach
    public void setUp() {
        CostService costService = new CostService();
        EnergySupplier energySupplier1 = new EnergySupplier(null, PRICE_PLAN_1_ID, BigDecimal.TEN, null);
        account = new Account("smart-meter-0", energySupplier1);

        final Map<String, Account> map = new HashMap<>();
        final ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        map.put(SMART_METER_ID, account);
        electricityReadings = electricityReadingsGenerator.generate(20);
        map.keySet().forEach(smartMeterId -> map.get(smartMeterId).addElectricityReadings(
                electricityReadings
        ));

        accountService = new AccountService(map, costService);
    }

    @Test
    public void givenTheSmartMeterIdReturnsTheAccount() throws Exception {
        assertThat(accountService.getAccountForSmartMeterId(SMART_METER_ID).get()).isEqualTo(account);
    }

    @Test
    public void givenUnknownSmartMeterIdReturnsEmptyOptionalAccount() throws Exception {
        assertThat(accountService.getAccountForSmartMeterId("uknown-smart-meter").isPresent()).isEqualTo(false);
    }

    @Test
    public void givenTheSmartMeterIdReturnsTheReadings() throws Exception {
        assertThat(accountService.getReadings(SMART_METER_ID).get()).isEqualTo(electricityReadings);
    }

    @Test
    public void givenUnknownSmartMeterIdReturnsEmptyOptionalReadings() throws Exception {
        assertThat(accountService.getReadings("unknown-smart-meter").isPresent()).isEqualTo(false);
    }

    @Test
    public void givenTheSmartMeterIdReturnsLastWeekCost() throws Exception {
        assertThat(accountService.calculateLastWeekUsageCost(SMART_METER_ID).isPresent()).isEqualTo(true);
    }

    @Test
    public void givenUnknownSmartMeterIdReturnsLastWeekCost() throws Exception {
        assertThat(accountService.calculateLastWeekUsageCost("unknown-smart-meter").isPresent()).isEqualTo(false);
    }

}
