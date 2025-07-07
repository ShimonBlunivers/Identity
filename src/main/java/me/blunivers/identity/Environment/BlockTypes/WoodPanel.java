package me.blunivers.identity.Environment.BlockTypes;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Environment.BlockType;
import me.blunivers.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WoodPanel extends BlockType {
    public WoodPanel() {
        super("WoodPanel", Material.SPRUCE_TRAPDOOR);
    }

    @Override
    public void update() {
    }

    @Override
    public void interact(PlayerInteractEvent event, int x, int y, int z, String world) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        event.setCancelled(true);
    }

    @Override
    public BlockInstance place(Player player, int x, int y, int z, String world){
        Block block = Bukkit.getWorld(world).getBlockAt(x, y, z);
        TrapDoor trapdoor = (TrapDoor) block.getBlockData();
        trapdoor.setOpen(true);
        block.setBlockData(trapdoor, true);

        return Identity.database.environment_placeCustomBlock(x + offset.x, y + offset.y,  z + offset.z, this, world);
    }
}
