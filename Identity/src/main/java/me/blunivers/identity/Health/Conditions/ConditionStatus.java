package me.blunivers.identity.Health.Conditions;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public enum ConditionStatus {
    UNTREATED(ChatColor.RED),
    LETHAL(ChatColor.DARK_RED),
    MEDICATED(ChatColor.DARK_GREEN);

    public static final int maximalStage = 100;
    private final ChatColor color;

    ConditionStatus(ChatColor _color) {
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
