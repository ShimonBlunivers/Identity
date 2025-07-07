package me.blunivers.identity.Environment;

import me.blunivers.identity.Identity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public abstract class BlockType {
    private static final Map<String, BlockType> blocks = new HashMap<>();

    public String name;
    public Material material;

    public Vector3i offset = new Vector3i(0, 0, 0);

    public BlockType(String _name, Material _material){
        blocks.put(_name, this);
        name = _name;
        material = _material;
    }

    public abstract void update();
    public abstract void interact(PlayerInteractEvent event, int x, int y, int z, String world);

    public static BlockType get(String blockName) {
        return blocks.get(blockName);
    }
    public static Map<String, BlockType> get() {
        return blocks;
    }

    public BlockInstance place(Player player, int x, int y, int z, String world){
        return Identity.database.environment_placeCustomBlock(x + offset.x, y + offset.y,  z + offset.z, this, world);
    }
    public boolean verifyMetadata(String metadata){
        return true;
    }
}
