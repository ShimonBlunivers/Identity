package me.blunivers.identity.Jobs;

public class JobInstance {
    protected int id;
    public JobType jobType;
    public String playerID;
    public int level;
    public int progression;

    public JobInstance(int id, JobType jobType, String playerID, int level, int progression){
        this.id = id;
        this.jobType = jobType;
        this.playerID = playerID;
        this.level = level;
        this.progression = progression;
    }
    @Override
    public String toString() {
        return "\nJobInstance" +
                "\nid=" + id +
                "\nname='" + jobType.displayName + '\'' +
                "\nplayerUUID='" + playerID + '\'' +
                "\nlevel=" + level +
                "\nprogression=" + progression + "\n";
    }
}
