package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.Health.HealthRegistry;

public class VaccineInstance {

    public HealthRegistry healthRegistry;
    public Vaccine vaccine;

    private int ticksBeforeExpiration;

    public boolean expired = false;

    public VaccineInstance(HealthRegistry _healthRegisty, Vaccine _vaccine) {
        healthRegistry = _healthRegisty;
        vaccine = _vaccine;
        ticksBeforeExpiration = vaccine.ticksBeforeExpiration;
    }

    public void update(int ticks) {
        ticksBeforeExpiration -= ticks;
        if (ticksBeforeExpiration <= 0) expired = true;
    }

    @Override
    public String toString() {
        return vaccine.displayName;
    }
}
