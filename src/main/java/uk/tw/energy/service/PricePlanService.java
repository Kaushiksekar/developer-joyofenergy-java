package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.EnergySupplier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricePlanService {

    private final List<EnergySupplier> energySuppliers;
    private final AccountService accountService;
    private final CostService costService;

    public PricePlanService(List<EnergySupplier> energySuppliers, AccountService accountService, CostService costService) {
        this.energySuppliers = energySuppliers;
        this.accountService = accountService;
        this.costService = costService;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = accountService.getReadings(smartMeterId);

        if (!electricityReadings.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(energySuppliers.stream().collect(
                Collectors.toMap(EnergySupplier::planName,
                        t -> costService.calculateCost(electricityReadings.get(), t.unitRate()))));
    }

}
