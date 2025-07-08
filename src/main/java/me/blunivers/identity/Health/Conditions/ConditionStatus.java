package me.blunivers.identity.Health.Conditions;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;

public enum ConditionStatus {
    UNTREATED(NamedTextColor.RED),
    LETHAL(NamedTextColor.DARK_RED),
    MEDICATED(NamedTextColor.DARK_GREEN);

    public static final int maximalStage = 100;
    private final NamedTextColor color;

    ConditionStatus(NamedTextColor _color) {
        color = _color;
    }

    public static ConditionStatus getStatus(ConditionInstance conditionInstance, ArrayList<MedicationInstance> medicationInstances){
        for (MedicationInstance medicationInstance : medicationInstances) if (!medicationInstance.expired && medicationInstance.medicationType.protectionAgainst.contains(conditionInstance.conditionType)) return MEDICATED;
        if (conditionInstance.stage > maximalStage - maximalStage / 4) return LETHAL;
        return UNTREATED;
    }

    @Override
    public String toString() {
        return color + "";

    }
}
