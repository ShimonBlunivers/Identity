package me.blunivers.identity.Jobs;

import me.blunivers.identity.DataItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public abstract class JobType extends DataItem {

    private static final Map<String, JobType> jobs = new HashMap<>();
    public int illegality;
    public int maxLevel = 10;
    public ArrayList<Rank> ranks = new ArrayList<>();

    public JobType(String _name, String _displayName, int _illegality) {
        jobs.put(_name, this);
        name = _name; // Name of the job
        displayName = _displayName;
        illegality = _illegality;       // 0 = legal; higher values mean higher illegality
    }
    public JobType(String _name, String _displayName) {
        jobs.put(_name, this);
        name = _name;
        displayName = _displayName;
        illegality = 0;
    }

    public abstract void work(Player player);

    public static Map<String, JobType> getJobs(){
        return jobs;
    }

    public static JobType get(String jobName){
        return jobs.get(jobName);
    }

}
