package me.blunivers.identity.Health.Conditions;

import org.bukkit.ChatColor;

public enum ConditionStatus {
    UNTREATED(ChatColor.RED),
    LETHAL(ChatColor.DARK_RED),
    MEDICATED(ChatColor.DARK_GREEN);

    public static int maximalStage = 100;
    private final ChatColor color;

    ConditionStatus(ChatColor _color) {
        color = _color;
    }

    @Override
    public String toString() {
        return color + "";

    }
}
