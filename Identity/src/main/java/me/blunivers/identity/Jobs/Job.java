package me.blunivers.identity.Jobs;

public abstract class Job {
    public String name;
    public int illegality;
    public Job(String _name, int _illegality) {
        JobManager.jobs.add(this);
        name = _name;                   // Name of the job
        illegality = _illegality;       // 0 = legal; higher values mean higher illegality
    }
}
