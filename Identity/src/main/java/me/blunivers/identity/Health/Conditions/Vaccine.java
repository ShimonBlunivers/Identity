package me.blunivers.identity.Health.Conditions;

import me.blunivers.identity.DataItem;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;
import me.blunivers.identity.Health.HealthManager;

public class Vaccine extends DataItem {

    public int ticksBeforeExpiration = 1728000;

    public Illness protectionAgainst;

    public Vaccine(String _displayName, Illness _against) {
        name = _against.name + "Vaccine";
        displayName = _displayName;
        protectionAgainst = _against;
        HealthManager.vaccines.add(this);
    }


}
