package me.blunivers.identity.Jobs.Illegal;


import me.blunivers.identity.Jobs.IllegalJob;
import me.blunivers.identity.Jobs.Occupation;
import org.bukkit.entity.Player;


public class Killer extends IllegalJob {
    public Killer(String _displayName) {
        super("Killer", 5);
        displayName = _displayName;
    }

    @Override
    public void work(Player player, Occupation occupation) {

    }
}
