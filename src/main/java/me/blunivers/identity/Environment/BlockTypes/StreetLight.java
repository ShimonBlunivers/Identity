package me.blunivers.identity.Environment.BlockTypes;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Environment.BlockType;
import me.blunivers.identity.Environment.EnvironmentManager;
import me.blunivers.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.player.PlayerInteractEvent;

public class StreetLight extends BlockType {

    private final Material onStreetLight = Material.REDSTONE_LAMP;
    private final Material offStreetLight = Material.REDSTONE_LAMP;

    public StreetLight() {
        super("StreetLight", Material.REDSTONE_LAMP);
        daylight = EnvironmentManager.singleton.isDay();
    }

    private boolean daylight;

    @Override
    public void update() {
        if (daylight != EnvironmentManager.singleton.isDay()) {
            daylight = EnvironmentManager.singleton.isDay();
            for (BlockInstance customBlock : Identity.database.environment_getCustomBlockInstances(this, "world")) {

                int x = customBlock.position.x;
                int y = customBlock.position.y;
                int z = customBlock.position.z;

                try {
                    Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);
                    if (daylight) {
                        block.setType(offStreetLight);
                    } else {
                        block.setType(onStreetLight);
                        Lightable lightable = (Lightable) block.getBlockData();
                        lightable.setLit(true);
                        block.setBlockData(lightable);
                    }
                } catch (Exception e) {
                    System.out.println("Update street lights exception! " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void interact(PlayerInteractEvent event, int x, int y, int z, String world) {
        return;
    }
}
