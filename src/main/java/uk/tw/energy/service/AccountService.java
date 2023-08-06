package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.Account;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.EnergySupplier;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class AccountService {

    public static final String WEEK_START = "weekStart";
    public static final String WEEK_END = "weekEnd";
    private final Map<String, Account> smartMeterAccountsMap;
    private final CostService costService;

    public AccountService(Map<String, Account> smartMeterAccountsMap, CostService costService) {
        this.smartMeterAccountsMap = smartMeterAccountsMap;
        this.costService = costService;
    }

    public Optional<Account> getAccountForSmartMeterId(String smartMeterId) {
        return Optional.ofNullable(smartMeterAccountsMap.get(smartMeterId));
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        if (!smartMeterAccountsMap.containsKey(smartMeterId))
            return Optional.empty();
        return Optional.ofNullable(smartMeterAccountsMap.get(smartMeterId).getElectricityReadings());
    }

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!smartMeterAccountsMap.containsKey(smartMeterId)) {
            // by default, attaching price-plan-0
            // TODO: have provision for customers to change this in AccountController
            EnergySupplier energySupplier = new EnergySupplier(
                    "Dr Evil's Dark Energy", "price-plan-0", BigDecimal.TEN, emptyList()
            );
            smartMeterAccountsMap.put(smartMeterId, new Account(smartMeterId, energySupplier, new ArrayList<>()));
        }
        smartMeterAccountsMap.get(smartMeterId).addElectricityReadings(electricityReadings);
    }

    public Optional<BigDecimal> calculateLastWeekUsageCost(String smartMeterId) {
        Optional<Account> account = getAccountForSmartMeterId(smartMeterId);
        if (account.isEmpty() || account.get().getEnergySupplier() == null) {
            return Optional.empty();
        }

        Map<String, Instant> weekRange = calculateWeekRange();
        Instant rangeStart = weekRange.get("weekStart");
        Instant rangeEnd = weekRange.get("weekEnd");

        EnergySupplier energySupplier = account.get().getEnergySupplier();
        
        List<ElectricityReading> electricityReadings = account.get().getElectricityReadings();
        electricityReadings = electricityReadings
             .stream()
             .filter(electricityReading -> 
                     electricityReading.time().isAfter(rangeStart) && electricityReading.time().isBefore(rangeEnd))
            .collect(Collectors.toList());
        
        return Optional.of(costService.calculateCost(electricityReadings, energySupplier.unitRate()));
    }

    private Map<String, Instant> calculateWeekRange() {
        Instant rangeStart = LocalDateTime.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant(ZoneOffset.UTC);

        Instant rangeEnd = LocalDateTime.now()
                .with(TemporalAdjusters.previous(DayOfWeek.SATURDAY))
                .withHour(11)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999)
                .toInstant(ZoneOffset.UTC);

        Map<String, Instant> map = new HashMap<>();
        map.put(WEEK_START, rangeStart);
        map.put(WEEK_END, rangeEnd);

        return map;
    }
    


}
