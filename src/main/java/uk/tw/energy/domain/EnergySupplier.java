package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record EnergySupplier(String energySupplier, String planName, BigDecimal unitRate,
                             List<EnergySupplier.PeakTimeMultiplier> peakTimeMultipliers) {


    @Override
    public List<EnergySupplier.PeakTimeMultiplier> peakTimeMultipliers() {
        return new ArrayList<>(peakTimeMultipliers);
    }

    public BigDecimal getPrice(LocalDateTime dateTime) {
        return peakTimeMultipliers.stream()
                .filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek()))
                .findFirst()
                .map(multiplier -> unitRate.multiply(multiplier.multiplier))
                .orElse(unitRate);
    }

    static class PeakTimeMultiplier {

        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }
}
