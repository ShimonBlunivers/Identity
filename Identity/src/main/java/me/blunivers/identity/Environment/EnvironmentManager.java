package me.blunivers.identity.Environment;

import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class EnvironmentManager extends Manager implements Runnable, Listener {
    private final static EnvironmentManager instance = new EnvironmentManager();

    public boolean raining;

    protected static String path = "";


    @Override
    public void load() {
        path = "environment.";
        raining = Bukkit.getWorld("world").hasStorm();
        customBlockInstances = (ArrayList<String>) file.getStringList(path + "data");
    }

    public static enum CustomBlockID {
        CanalLid,

    }

    private ArrayList<String> customBlockInstances = new ArrayList<>();



    public static EnvironmentManager getInstance() {
        return instance;
    }

    public static void givePlayerIdentityStick(Player player) {
        ItemStack item = new ItemStack(Material.STICK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("IdentityStick" + Identity.identificator);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Byla ti darována mocná tyčka identit!");
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
    public void useIdentityStick(PlayerInteractEvent event){


        if (event.getItem() == null || event.getClickedBlock() == null || !event.getPlayer().isOp()) return;

        ItemStack itemStack = event.getItem();
        if (itemStack.getType().equals(Material.STICK) && itemStack.getItemMeta().getDisplayName().equals("IdentityStick" + Identity.identificator)){
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            event.setCancelled(true);

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            String identityStickString = ChatColor.LIGHT_PURPLE + "<IdentityStick> ";

            for (String customBlock : customBlockInstances) {
                String[] splitted = customBlock.split(";");


                int X = Integer.parseInt(splitted[1]);
                int Y = Integer.parseInt(splitted[2]);
                int Z = Integer.parseInt(splitted[3]);

                if (x == X && y == Y && Z == z) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        givePlayerCustomBlock(player, CustomBlockID.valueOf(splitted[0]), block.getType());
                        player.sendMessage(identityStickString + ChatColor.WHITE + "Do inventáře ti byl přidán block "+ ChatColor.GREEN + splitted[0]);

                        return;
                    }
                    else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        player.sendMessage(identityStickString + ChatColor.GREEN + splitted[0]);
                        return;
                    }
                }
            }
            player.sendMessage(identityStickString + ChatColor.RED + "Vanilla block");


        }
    }


    @EventHandler
    public void invisibleItemFrameProtection(PlayerInteractEntityEvent event){
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.ITEM_FRAME) {
            ItemFrame itemFrame = (ItemFrame) entity;
            if (!itemFrame.isVisible())
                if (!itemFrame.getItem().equals(new ItemStack(Material.AIR, 1))) event.setCancelled(true);
        }
    }





    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp()) return;
        ItemMeta meta = event.getItemInHand().getItemMeta();
        Block block = event.getBlock();
        for (CustomBlockID id : CustomBlockID.values()){
            if (meta.getDisplayName().equalsIgnoreCase(id.name() + Identity.identificator) ){
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
        meta.setDisplayName(id.name() + Identity.identificator);
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
