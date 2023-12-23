package me.blunivers.identity.Jobs;


import me.blunivers.identity.Jobs.Illegal.*;
import me.blunivers.identity.Jobs.Legal.*;

import java.util.ArrayList;


public class JobManager {

    public static final ArrayList<Job> jobs = new ArrayList<>();

    // Legal jobs
    private static final Job doctor = new Doctor("Doctor");

    // Illegal jobs
    private static final Job killer = new Killer("Doctor");
}
