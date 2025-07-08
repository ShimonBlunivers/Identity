package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.Health.Conditions.Illnesses.Illness;

public class ConditionInstance {

    public int id;
    public ConditionType conditionType;
    public String playerID;
    public int stage;
    public boolean hidden;


    public ConditionInstance(int id, ConditionType conditionType, String playerID, int stage, boolean hidden) {
        this.id = id;
        this.conditionType = conditionType;
        this.playerID = playerID;
        this.stage = stage;
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        if (conditionType instanceof Illness illness) {
            return illness.displayName + " [" + stage + "/" + ConditionStatus.maximalStage + "]";
        }
        return conditionType.displayName;
    }

    public float getSymptomMultiplier(){
        return ((float) stage / 20) + 1;
    }
}
