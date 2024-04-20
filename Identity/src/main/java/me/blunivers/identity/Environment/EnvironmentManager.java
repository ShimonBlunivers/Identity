package me.blunivers.identity.Environment;

import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EnvironmentManager extends Manager implements Runnable, Listener {
    private final static EnvironmentManager instance = new EnvironmentManager();

    public boolean raining;

    protected static String path = "";


    @Override
    public void load() {
        path = "environment.";
        raining = Bukkit.getWorld("world").hasStorm();
        updateCanals();
    }



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
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        Identity.database.environment_removeCustomBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
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

            for (CustomBlockInstance customBlock : Identity.database.environment_getCustomBlockInstances()) {
                int X = customBlock.position.x;
                int Y = customBlock.position.y;
                int Z = customBlock.position.z;

                if (x == X && y == Y && Z == z) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        givePlayerCustomBlock(player, customBlock.block_id, block.getType());
                        player.sendMessage(identityStickString + ChatColor.WHITE + "Do inventáře ti byl přidán block "+ ChatColor.GREEN + customBlock.block_id.name());

                        return;
                    }
                    else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        player.sendMessage(identityStickString + ChatColor.GREEN + customBlock.block_id.name());
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
        for (CustomBlockInstance.BLOCK_ID block_id : CustomBlockInstance.BLOCK_ID.values()){
            if (meta.getDisplayName().equalsIgnoreCase(block_id.name() + Identity.identificator) ){
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                    return;
                }
                Identity.database.environment_placeCustomBlock(block.getX(), block.getY(), block.getZ(), block_id, block.getWorld().getName());
            }
        }
    }
    public static void givePlayerCustomBlock(Player player, CustomBlockInstance.BLOCK_ID id, Material material) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(id.name() + Identity.identificator);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    @Override
    public void run() {
        if (raining != Bukkit.getWorld("world").hasStorm()) updateCanals();
        raining = Bukkit.getWorld("world").hasStorm();
    }

    public void updateCanals() {
        for (CustomBlockInstance customBlock : Identity.database.environment_getCustomBlockInstances(CustomBlockInstance.BLOCK_ID.CanalLid, "world")) {

            int x = customBlock.position.x;
            int y = customBlock.position.y;
            int z = customBlock.position.z;

            try {
                Block block = Bukkit.getWorld("world").getBlockAt(x, y, z);
                TrapDoor trapdoor = (TrapDoor) block.getBlockData();
                trapdoor.setWaterlogged(Bukkit.getWorld("world").hasStorm());
                block.setBlockData(trapdoor, true);
                block = Bukkit.getWorld("world").getBlockAt(x, y - 1, z);
                if (Bukkit.getWorld("world").hasStorm()) block.setType(Material.WATER, true);
                else block.setType(Material.AIR, true);

            }catch (Exception e){
                System.out.println("Update canals exception! " + e.getMessage());
            }
        }
    }


}
