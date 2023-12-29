package me.blunivers.identity.Health;

import me.blunivers.identity.DataItem;
import me.blunivers.identity.Health.Conditions.Illnesses.Illness;

public class Vaccine extends DataItem {

    public int ticksBeforeExpiration = 1728000;

    public Illness protectionAgainst;

    public Vaccine(String _displayName, Illness _against) {
        displayName = _displayName;
        protectionAgainst = _against;
        HealthManager.vaccines.add(this);
    }


}
