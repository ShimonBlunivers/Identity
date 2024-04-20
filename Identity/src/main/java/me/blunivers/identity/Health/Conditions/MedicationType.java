package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.DataItem;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.HealthManager;

import java.util.ArrayList;
import java.util.HashMap;

public class MedicationType extends DataItem {

    public static final HashMap<String, MedicationType> medications = new HashMap<>();
    public int timeBeforeExpiration = 72000; // Ticks (72000t = 1 hour)

    public ArrayList<ConditionType> protectionAgainst = new ArrayList<>();

    public MedicationType(String _name, String _displayName, ConditionType _against) {
        name = _name;
        displayName = _displayName;
        protectionAgainst.add(_against);
        medications.put(name, this);
    }
    public MedicationType(String _name, String _displayName, ArrayList<ConditionType> _against) {
        name = _name;
        displayName = _displayName;
        protectionAgainst = _against;
        medications.put(name, this);
    }

    public static MedicationType get(String medicationName){
        return medications.get(medicationName);
    }
}
