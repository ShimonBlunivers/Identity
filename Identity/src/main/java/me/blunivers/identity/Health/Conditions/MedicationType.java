package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.DataItem;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.HealthManager;

import java.util.HashMap;

public class MedicationType extends DataItem {

    public static final HashMap<String, MedicationType> medications = new HashMap<>();
    public int timeBeforeExpiration = 72000; // Ticks (72000t = 1 hour)

    public Illness protectionAgainst;

    public MedicationType(String _displayName, Illness _against) {
        name = _against.name + "_medication";
        displayName = _displayName;
        protectionAgainst = _against;
        medications.put(name, this);
    }

    public static MedicationType get(String medicationName){
        return medications.get(medicationName);
    }
}
