package me.blunivers.identity.Jobs;

public class Occupation {
    public Job job;
    public int progression;

    public float requiredExperience;
    public float currentExperience;

    public Occupation(Job _job, int _progression) {
        job = _job;
        progression = _progression;
    }

    @Override
    public String toString() {
        return "Job: " + job.name + ", Progression: " + progression;
    }
}
