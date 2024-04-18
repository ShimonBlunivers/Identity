package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.HealthManager;
import me.blunivers.identity.Health.HealthRegistry;

import java.util.Random;

public class ConditionInstance {

    public HealthRegistry healthRegistry;
    public Condition condition;
    public int stage;
    int ticksWithCondition = 0;
    public int symptomsCooldown = 0;
    public boolean finalStage = false;
    public boolean medicated = false;
    public boolean hidden = false; // True

    public ConditionInstance(HealthRegistry _healthRegistry, Condition _condition) {
        healthRegistry = _healthRegistry;
        condition = _condition;
        stage = 1;

        if (condition instanceof Illness) {
            Illness illness = (Illness) condition;
            Random random = new Random();
            symptomsCooldown = random.nextInt((int) illness.symptomsTimer / 2);
        }
    }

    public ConditionStatus getStatus() {
        if (medicated) return ConditionStatus.MEDICATED;
        if (!(condition instanceof Illness)) return ConditionStatus.UNTREATED;
        if (condition instanceof Illness && (float)stage / (float)((Illness) condition).lethalStage >= 3f/4f) return ConditionStatus.LETHAL;
        return ConditionStatus.UNTREATED;
    }

    public void setStage(int _stage) {
        if (condition instanceof Illness) {
            Illness illness = (Illness) condition;
            stage = _stage;
            ticksWithCondition = _stage * illness.ticksToNextStage;
        }
    }

    public void update(int ticks) {
        ticksWithCondition += ticks;
        if (condition instanceof Illness) {
            Illness illness = (Illness) condition;
            symptomsCooldown += ticks;
            if (symptomsCooldown >= illness.symptomsTimer){
                symptomsCooldown = 0;
                Random random = new Random();

                if (random.nextInt(Math.max((illness.lethalStage - stage) / 10, 1)) == 0){
                    if (hidden) hidden = false;
                    illness.symptoms(healthRegistry.player);
                }
            }
            if (stage >= illness.lethalStage) finalStage = true;

            if (!finalStage && ticksWithCondition >= stage * illness.ticksToNextStage) {
                if (medicated) stage -= 4;
                else stage++;
            }
            if (stage < 1) cure();

        }
    }

    public void cure() {
        HealthManager.removeConditionFromPlayer(healthRegistry.player, this);
    }

    @Override
    public String toString() {
        if (condition instanceof Illness) {
            Illness illness = (Illness) condition;
            return getStatus().toString() + illness.displayName + " [" + stage + "/" + illness.lethalStage + "]";
        }
        return condition.displayName;
    }
}
