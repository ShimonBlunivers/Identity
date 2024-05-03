package me.blunivers.identity.Environment.BlockTypes;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Environment.BlockType;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Menus.DoorMaker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.joml.Vector3i;


public class SecureDoor extends BlockType {

    public SecureDoor() {
        super("SecureDoor", Material.WARPED_DOOR);
        offset = new Vector3i(0, 1, 0);
    }

    @Override
    public void update() {
        for (BlockInstance blockInstance : Identity.database.environment_getCustomBlockInstances(this, "world")){
        }
    }

    @Override
    public void interact(PlayerInteractEvent event, int x, int y, int z, String world) {
        BlockInstance blockInstance = Identity.database.environment_getCustomBlockInstance(x, y, z, world);
        if (blockInstance == null) return;

        System.out.println(blockInstance.metadata);

        // TODO: Přidat verifier informací na cedulce
    }

    @Override
    public BlockInstance place(Player player, int x, int y, int z, String world) {
        BlockInstance blockInstance = super.place(player, x, y, z, world);
        new DoorMaker(player, blockInstance);
        return blockInstance;
    }
}
