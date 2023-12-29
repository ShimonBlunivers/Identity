package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.Health.Vaccine;

public class VaccineInstance {

    public Vaccine vaccine;

    private int ticksBeforeExpiration;

    public boolean expired = false;

    public VaccineInstance(Vaccine _vaccine) {
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
