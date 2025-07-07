package me.blunivers.identity.Environment;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.joml.Vector3i;

public class BlockInstance {

    protected int id;
    public BlockType blockType;
    public int x;
    public int y;
    public int z;
    public Vector3i position ;
    public World world;
    public String metadata;

    public Block block;
    public Block offsetted_block;

    public BlockInstance(int id, BlockType blockType, Vector3i position, World world, String metadata) {
        this.id = id;
        this.blockType = blockType;
        this.position = position;
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
        this.world = world;
        this.metadata = metadata;

        this.block = world.getBlockAt(x, y, z);
        this.offsetted_block = world.getBlockAt(x - blockType.offset.x, y - blockType.offset.y, z - blockType.offset.z);

    }


    @Override
    public String toString() {
        return "CustomBlockInstance{" +
                "id=" + id +
                ", block_type=" + blockType.toString() +
                ", position=" + position.toString() +
                ", world='" + world.getName() + '\'' +
                '}';
    }
}
