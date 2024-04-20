package me.blunivers.identity.Environment;

import org.joml.Vector3i;

public class CustomBlockInstance {
    public static enum BLOCK_ID {
        CanalLid,

    }
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
