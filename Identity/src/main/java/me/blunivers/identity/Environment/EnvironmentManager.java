package me.blunivers.identity.Environment;

import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class EnvironmentManager extends Manager implements Runnable, Listener {
    private final static EnvironmentManager instance = new EnvironmentManager();

    public boolean raining;

    @Override
    public void load() {
        path = "environment.";
        raining = Bukkit.getWorld("world").hasStorm();
        customBlockInstances = (ArrayList<String>) file.getStringList(path + "data");
    }

    public static enum CustomBlockID {
        CanalLid,
        AAAA

    }

    private ArrayList<String> customBlockInstances = new ArrayList<>();



    public static EnvironmentManager getInstance() {
        return instance;
    }


    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        for (String customBlock : customBlockInstances) {
            String[] splitted = customBlock.split(";");

            if (splitted[1].equals(Integer.toString(block.getX())) && splitted[2].equals(Integer.toString(block.getY())) && splitted[3].equals(Integer.toString(block.getZ()))){
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                    return;
                }
                customBlockInstances.remove(customBlock);
                String directPath = path + "data";
                file.set(directPath, customBlockInstances);
                Identity.plugin.saveConfig();
            }
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp()) return;
        ItemMeta meta = event.getItemInHand().getItemMeta();
        Block block = event.getBlock();
        for (CustomBlockID id : CustomBlockID.values()){
            if (meta.getDisplayName().equalsIgnoreCase(id.name() + " Identity-Plugin") ){
                String directPath = path + "data";
                if (!file.isSet(directPath)) file.set(directPath, new ArrayList<String>());
                customBlockInstances = (ArrayList<String>) file.getStringList(directPath);
                customBlockInstances.add(id.name() + ";" + block.getX() + ";" + block.getY() + ";" + block.getZ());
                file.set(directPath, customBlockInstances);
                Identity.plugin.saveConfig();
            }
        }
    }
    public static void givePlayerCustomBlock(Player player, CustomBlockID id, Material material) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(id.name() + " Identity-Plugin");

        item.setItemMeta(meta);

        player.getInventory().addItem(item);
    }

    public static CustomBlockID getCustomBlockID(Block block) {
        return null;
    }

    @Override
    public void run() {
        if (raining != Bukkit.getWorld("world").hasStorm()) updateCanal();
        raining = Bukkit.getWorld("world").hasStorm();


    }

    public void updateCanal() {
        for (String customBlock : customBlockInstances) {
            String[] splitted = customBlock.split(";");
            if (!splitted[0].equals(CustomBlockID.CanalLid.name())) continue;

            int x = Integer.parseInt(splitted[1]);
            int y = Integer.parseInt(splitted[2]);
            int z = Integer.parseInt(splitted[3]);

            Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);
            TrapDoor trapdoor = (TrapDoor) block.getBlockData();
            trapdoor.setWaterlogged(Bukkit.getWorld("world").hasStorm());
            block.setBlockData(trapdoor, true);


            block = Bukkit.getWorld("world").getBlockAt(x, y - 1, z);
            if (Bukkit.getWorld("world").hasStorm()) block.setType(Material.WATER, true);
            else block.setType(Material.AIR, true);
        }
    }


}
