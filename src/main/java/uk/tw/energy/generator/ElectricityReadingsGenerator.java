package uk.tw.energy.generator;

import uk.tw.energy.domain.ElectricityReading;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ElectricityReadingsGenerator {

    public List<ElectricityReading> generate(int number) {
        List<ElectricityReading> readings = new ArrayList<>();
        Instant now = Instant.now();

        Random readingRandomiser = new Random();
        for (int i = 0; i < number; i++) {
            double positiveRandomValue = Math.abs(readingRandomiser.nextGaussian());
            BigDecimal randomReading = BigDecimal.valueOf(positiveRandomValue).setScale(4, RoundingMode.CEILING);
            ElectricityReading electricityReading;
            if (i%2 == 0)
                electricityReading = new ElectricityReading(now.minusSeconds(i * 10), randomReading);
            else
                electricityReading = new ElectricityReading(now
                        .minus(Period.ofDays(readingRandomiser.nextInt(7,10))), randomReading);
            readings.add(electricityReading);
        }

        readings.sort(Comparator.comparing(ElectricityReading::time));
        return readings;
    }
}
