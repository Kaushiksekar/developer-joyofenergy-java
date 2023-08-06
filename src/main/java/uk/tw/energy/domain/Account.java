package uk.tw.energy.domain;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private final String smartMeterId;

    // When we retrieve from DB in production like system, we get the associated energy supplier
    private final EnergySupplier energySupplier;

    // When we retrieve from DB in production like system, we get the associated electricity readings
    private final List<ElectricityReading> electricityReadings;

    public Account(String smartMeterId, EnergySupplier energySupplier) {
        this.smartMeterId = smartMeterId;
        this.energySupplier = energySupplier;
        this.electricityReadings = new ArrayList<>();
    }

    public Account(String smartMeterId, EnergySupplier energySupplier, List<ElectricityReading> electricityReadings) {
        this.smartMeterId = smartMeterId;
        this.energySupplier = energySupplier;
        this.electricityReadings = electricityReadings;
    }

    public String getSmartMeterId() {
        return smartMeterId;
    }

    public EnergySupplier getEnergySupplier() {
        return energySupplier;
    }

    public List<ElectricityReading> getElectricityReadings() {
        return electricityReadings;
    }

    public void addElectricityReadings(List<ElectricityReading> electricityReadings) {
        this.electricityReadings.addAll(electricityReadings);
    }

    @Override
    public String toString() {
        return "Account{" +
                "smartMeterId='" + smartMeterId + '\'' +
                ", energySupplier=" + energySupplier +
                ", electricityReadings=" + electricityReadings +
                '}';
    }
}
