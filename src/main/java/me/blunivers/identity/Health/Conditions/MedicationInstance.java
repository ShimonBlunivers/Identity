package me.blunivers.identity.Health.Conditions;


public class MedicationInstance {

    public int id;
    public MedicationType medicationType;
    public String playerID;
    public int timeBeforeExpiration;
    public boolean expired;


    public MedicationInstance(int id, MedicationType medicationType, String playerID, int timeBeforeExpiration, boolean expired) {
        this.id = id;
        this.medicationType = medicationType;
        this.playerID = playerID;
        this.timeBeforeExpiration = timeBeforeExpiration;
        this.expired = expired;
    }

    @Override
    public String toString() {
        return medicationType.displayName;
    }
}
