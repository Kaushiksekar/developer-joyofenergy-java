package uk.tw.energy.domain;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class EnergySupplierTest {

    private final String ENERGY_SUPPLIER_NAME = "Energy Supplier Name";

    @Test
    public void shouldReturnTheEnergySupplierGivenInTheConstructor() {
        EnergySupplier pricePlan = new EnergySupplier(ENERGY_SUPPLIER_NAME, null, null, null);

        assertThat(pricePlan.energySupplier()).isEqualTo(ENERGY_SUPPLIER_NAME);
    }

    @Test
    public void shouldReturnTheBasePriceGivenAnOrdinaryDateTime() throws Exception {
        LocalDateTime normalDateTime = LocalDateTime.of(2017, Month.AUGUST, 31, 12, 0, 0);
        EnergySupplier.PeakTimeMultiplier peakTimeMultiplier = new EnergySupplier.PeakTimeMultiplier
                (DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        EnergySupplier energySupplier = new EnergySupplier(null, null, BigDecimal.ONE,
                Collections.singletonList(peakTimeMultiplier));

        BigDecimal price = energySupplier.getPrice(normalDateTime);

        assertThat(price).isCloseTo(BigDecimal.ONE, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReturnAnExceptionPriceGivenExceptionalDateTime() throws Exception {
        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
        EnergySupplier.PeakTimeMultiplier peakTimeMultiplier = new EnergySupplier.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        EnergySupplier energySupplier = new EnergySupplier(null, null, BigDecimal.ONE,
                Collections.singletonList(peakTimeMultiplier));

        BigDecimal price = energySupplier.getPrice(exceptionalDateTime);

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }

    @Test
    public void shouldReceiveMultipleExceptionalDateTimes() throws Exception {
        LocalDateTime exceptionalDateTime = LocalDateTime.of(2017, Month.AUGUST, 30, 23, 0, 0);
        EnergySupplier.PeakTimeMultiplier peakTimeMultiplier = new EnergySupplier.PeakTimeMultiplier(DayOfWeek.WEDNESDAY, BigDecimal.TEN);
        EnergySupplier.PeakTimeMultiplier otherPeakTimeMultiplier = new EnergySupplier.PeakTimeMultiplier(DayOfWeek.TUESDAY, BigDecimal.TEN);
        List<EnergySupplier.PeakTimeMultiplier> peakTimeMultipliers = Arrays.asList(peakTimeMultiplier, otherPeakTimeMultiplier);
        EnergySupplier energySupplier = new EnergySupplier(null, null, BigDecimal.ONE, peakTimeMultipliers);

        BigDecimal price = energySupplier.getPrice(exceptionalDateTime);

        assertThat(price).isCloseTo(BigDecimal.TEN, Percentage.withPercentage(1));
    }
}
