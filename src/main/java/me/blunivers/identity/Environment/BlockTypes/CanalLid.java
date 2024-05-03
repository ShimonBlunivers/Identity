package me.blunivers.identity.Environment.BlockTypes;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Environment.BlockType;
import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.player.PlayerInteractEvent;

public class CanalLid extends BlockType {
    public CanalLid() {
        super("CanalLid", Material.IRON_TRAPDOOR);
        raining = Bukkit.getWorld("world").hasStorm();
    }

    private boolean raining;

    @Override
    public void update() {
        if (raining != Bukkit.getWorld("world").hasStorm()) {
            raining = Bukkit.getWorld("world").hasStorm();

            for (BlockInstance customBlock : Identity.database.environment_getCustomBlockInstances(this, "world")) {

                int x = customBlock.position.x;
                int y = customBlock.position.y;
                int z = customBlock.position.z;

                try {
                    Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);
                    TrapDoor trapdoor = (TrapDoor) block.getBlockData();
                    trapdoor.setWaterlogged(raining);
                    block.setBlockData(trapdoor, true);
                    block = Bukkit.getWorld("world").getBlockAt(x, y - 1, z);
                    if (raining) block.setType(Material.WATER, true);
                    else block.setType(Material.AIR, true);

                } catch (Exception e) {
                    System.out.println("Update canals exception! " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void interact(PlayerInteractEvent event, int x, int y, int z, String world) {

    }
}
