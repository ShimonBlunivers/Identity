package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.Health.Conditions.Illnesses.Illness;

public class ConditionInstance {

    Condition condition;
    int stage;
    int ticksWithCondition = 0;
    public boolean finalStage = false;
    public ConditionInstance(Condition _condition) {
        condition = _condition;
        stage = 1;
    }

    public void update(int ticks) {
        ticksWithCondition += ticks;
        if (condition instanceof Illness) {
            Illness illness = (Illness) condition;
            if (stage >= illness.lethalStage) finalStage = true;
            if (!finalStage && ticksWithCondition >= stage * illness.ticksToNextStage) stage++;
        }
    }

    @Override
    public String toString() {
        if (condition instanceof Illness) {
            Illness illness = (Illness) condition;
            return illness.displayName + " [" + stage + "/" + illness.lethalStage + "]";
        }
        return condition.displayName;
    }
}
