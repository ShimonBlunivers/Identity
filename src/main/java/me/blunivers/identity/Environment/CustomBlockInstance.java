package me.blunivers.identity.Environment;

import org.bukkit.Material;
import org.joml.Vector3i;

import java.util.Map;

public class CustomBlockInstance {
    public static enum BLOCK_ID {
        CanalLid,
        StreetLight,
    }

    public static Map<BLOCK_ID, Material> blockMaterial = Map.of(
            BLOCK_ID.CanalLid, Material.IRON_TRAPDOOR,
            BLOCK_ID.StreetLight, Material.REDSTONE_LAMP
    );
    protected int id;
    public BLOCK_ID block_id;
    public Vector3i position;
    public String world;

    public CustomBlockInstance(int id, BLOCK_ID block_id, Vector3i position, String world) {
        this.id = id;
        this.block_id = block_id;
        this.position = position;
        this.world = world;
    }
    public CustomBlockInstance(int id, String block_id, Vector3i position, String world) {
        this.id = id;
        this.block_id = BLOCK_ID.valueOf(block_id);
        this.position = position;
        this.world = world;
    }

    @Override
    public String toString() {
        return "CustomBlockInstance{" +
                "id=" + id +
                ", block_id=" + block_id +
                ", position=" + position +
                ", world='" + world + '\'' +
                '}';
    }
}
