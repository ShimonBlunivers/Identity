package me.blunivers.identity.Jobs;

import me.blunivers.identity.DataItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class Job extends DataItem {
    public int illegality;
    public int maxLevel = 10;
    public ArrayList<Rank> ranks = new ArrayList<>();

    public Job(String _name, int _illegality) {
        JobManager.jobs.add(this);
        name = _name;                   // Name of the job
        illegality = _illegality;       // 0 = legal; higher values mean higher illegality
    }

    public abstract void work(Player player, Occupation occupation);

}
