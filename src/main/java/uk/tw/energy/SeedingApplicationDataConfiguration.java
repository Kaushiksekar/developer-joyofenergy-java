package uk.tw.energy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.Account;
import uk.tw.energy.domain.EnergySupplier;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Configuration
public class SeedingApplicationDataConfiguration {

    private static final String MOST_EVIL_PRICE_PLAN_ID = "price-plan-0";
    private static final String RENEWABLES_PRICE_PLAN_ID = "price-plan-1";
    private static final String STANDARD_PRICE_PLAN_ID = "price-plan-2";

    @Bean
    public Map<String, Account> accountMap() {
        final Map<String, Account> map = new HashMap<>();
        final ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();

        map.put("smart-meter-0", new Account("smart-meter-0", new EnergySupplier(
                "Dr Evil's Dark Energy", MOST_EVIL_PRICE_PLAN_ID, BigDecimal.TEN, emptyList())));
        map.put("smart-meter-1", new Account("smart-meter-1", new EnergySupplier(
                "The Green Eco", RENEWABLES_PRICE_PLAN_ID, BigDecimal.TEN, emptyList())));
        map.put("smart-meter-2", new Account("smart-meter-2", new EnergySupplier(
                "Dr Evil's Dark Energy", MOST_EVIL_PRICE_PLAN_ID, BigDecimal.TEN, emptyList())));
        map.put("smart-meter-3", new Account("smart-meter-3", new EnergySupplier(
                "Power for Everyone", STANDARD_PRICE_PLAN_ID, BigDecimal.TEN, emptyList())));
        map.put("smart-meter-4", new Account("smart-meter-4", new EnergySupplier(
                "The Green Eco", RENEWABLES_PRICE_PLAN_ID, BigDecimal.TEN, emptyList())));

        map.keySet().forEach(smartMeterId -> map.get(smartMeterId).addElectricityReadings(
                electricityReadingsGenerator.generate(20)
        ));

        return map;
    }

    @Bean
    public List<EnergySupplier> pricePlans() {
        final List<EnergySupplier> energySuppliers = new ArrayList<>();
        energySuppliers.add(new EnergySupplier("Dr Evil's Dark Energy", MOST_EVIL_PRICE_PLAN_ID, BigDecimal.TEN, emptyList()));
        energySuppliers.add(new EnergySupplier("The Green Eco", RENEWABLES_PRICE_PLAN_ID, BigDecimal.valueOf(2), emptyList()));
        energySuppliers.add(new EnergySupplier("Power for Everyone", STANDARD_PRICE_PLAN_ID, BigDecimal.ONE, emptyList()));
        return energySuppliers;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
