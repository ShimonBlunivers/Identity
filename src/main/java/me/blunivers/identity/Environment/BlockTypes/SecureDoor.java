package me.blunivers.identity.Environment.BlockTypes;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Environment.BlockType;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Jobs.JobInstance;
import me.blunivers.identity.Jobs.JobType;
import me.blunivers.identity.Menus.DoorMaker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.joml.Vector3i;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;

public class SecureDoor extends BlockType {
    public SecureDoor() {
        super("SecureDoor", Material.WARPED_DOOR);
        offset = new Vector3i(0, 1, 0);
    }

    @Override
    public void update() {
        // for (BlockInstance blockInstance :
        // Identity.database.environment_getCustomBlockInstances(this, "world")){
        // }
    }

    @Override
    public void interact(PlayerInteractEvent event, int x, int y, int z, String world) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        BlockInstance blockInstance = Identity.database.environment_getCustomBlockInstance(x, y, z, world);
        if (blockInstance == null)
            return;
        // if (event.getPlayer().isOp()) return;
        if (blockInstance.metadata.isEmpty()) {
            event.setCancelled(true);
            return;
        }
        ArrayList<JobInstance> playerJobs = Identity.database.jobs_getJobInstances(event.getPlayer());
        ArrayList<JobType> playerJobTypes = new ArrayList<>();

        for (JobInstance playerJob : playerJobs)
            playerJobTypes.add(playerJob.jobType);

        HashMap<JobType, Integer> metadata = convertMetadata(blockInstance.metadata);

        for (JobType jobType : metadata.keySet()) {
            if (!playerJobTypes.contains(jobType)) {
                event.setCancelled(true);
                return;
            } else {
                for (JobInstance jobInstance : playerJobs) {
                    if (jobInstance.jobType == jobType) {
                        if (jobInstance.level < metadata.get(jobType)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public BlockInstance place(Player player, int x, int y, int z, String world) {
        BlockInstance blockInstance = super.place(player, x, y, z, world);
        new DoorMaker(player, blockInstance);
        return blockInstance;
    }

    @Override
    public boolean verifyMetadata(String metadata) {
        if (metadata.isEmpty()) {
            return true;
        }
        for (String line : metadata.split(",")) {
            String[] entry = line.split(":");
            if (entry.length != 2) {
                return false;
            }
            if (!JobType.getJobs().containsKey(entry[0])) {
                return false;
            }
            if (!isNumeric(entry[1])) {
                return false;
            }
        }
        return true;
    }

    public HashMap<JobType, Integer> convertMetadata(String metadata) {
        HashMap<JobType, Integer> result = new HashMap<>();
        for (String line : metadata.split(",")) {
            String[] entry = line.split(":");
            if (entry.length == 2)
                result.put(JobType.get(entry[0]), Integer.parseInt(entry[1]));
        }
        return result;
    }

    public static boolean isNumeric(String s) {
        ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(s, pos);
        return s.length() == pos.getIndex();
    }
}
