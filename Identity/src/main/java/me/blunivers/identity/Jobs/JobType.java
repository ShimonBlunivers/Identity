package me.blunivers.identity.Jobs;

import me.blunivers.identity.DataItem;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


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

    public static void loadJobTypes(){
        Reflections reflections = new Reflections("me.blunivers.identity"); // Specify the package to scan
        Set<Class<? extends JobType>> jobClasses = reflections.getSubTypesOf(JobType.class);
        for (Class<? extends JobType> jobClass : jobClasses) {
            try {
                jobClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("Failed to load job types! " + e.getMessage());
            }
        }
    }

    public static Map<String, JobType> getJobs(){
        return jobs;
    }

    public static JobType get(String jobName){
        return jobs.get(jobName);
    }

}
