package me.blunivers.identity.Environment;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
import org.joml.Vector3i;
import org.reflections.Reflections;

import java.nio.channels.WritePendingException;
import java.util.Set;


public class EnvironmentManager extends Manager implements Runnable, Listener {
    private final static EnvironmentManager instance = new EnvironmentManager();

    protected static String path = "";


    @Override
    public void load() {
        path = "environment.";
        Reflections reflections = new Reflections("me.blunivers.identity"); // Specify the package to scan
        Set<Class<? extends BlockType>> blockClasses = reflections.getSubTypesOf(BlockType.class);
        for (Class<? extends BlockType> blockClass : blockClasses) {
            try {
                blockClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("Failed to block types! " + e.getMessage());
            }
        }
        updateBlockInstances();
    }


    public boolean isDay() {
        long time = Bukkit.getWorld("world").getTime();
        return time > 0 && time < 12300;
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
    public void onDestroy(BlockDestroyEvent event) {
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

            for (BlockInstance customBlock : Identity.database.environment_getCustomBlockInstances()) {
                int X = customBlock.position.x;
                int Y = customBlock.position.y;
                int Z = customBlock.position.z;

                if (x == X && y == Y && Z == z) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        player.getInventory().addItem(getCustomItem(customBlock.blockType));
                        player.sendMessage(identityStickString + ChatColor.WHITE + "Do inventáře ti byl přidán block "+ ChatColor.GREEN + customBlock.blockType.name);

                        return;
                    }
                    else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        player.sendMessage(identityStickString + ChatColor.GREEN + customBlock.blockType.name);
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

        if (meta.getDisplayName().contains(Identity.identificator)) {
            BlockType blockType = BlockType.get(meta.getDisplayName().replace(Identity.identificator, ""));
            if (blockType != null){
                if (!event.getPlayer().isOp()) {
                    event.setCancelled(true);
                    return;
                }
                blockType.place(event.getPlayer(), block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
            }
        }

    }
    public static ItemStack getCustomItem(BlockType blockType){
        ItemStack item = new ItemStack(blockType.material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(blockType.name + Identity.identificator);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack getCustomItemWithoutLabel(BlockType blockType){
        ItemStack item = new ItemStack(blockType.material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(blockType.name);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void run() {
        updateBlockInstances();
    }

    public void updateBlockInstances() {
        for (BlockType blockType : BlockType.get().values()){
            blockType.update();
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        for (BlockType blockType : BlockType.get().values()){
            Block block  = event.getClickedBlock();
            if (block != null && block.getType() == blockType.material){
                blockType.interact(event, block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
                if (!(blockType.offset.x == 0 && blockType.offset.y == 0 && blockType.offset.z == 0)){
                    Block block_offsetted = event.getPlayer().getWorld().getBlockAt(new Location(event.getPlayer().getWorld(), block.getX() + blockType.offset.x, block.getY() + blockType.offset.y, block.getZ() + blockType.offset.z));
                    if (block_offsetted != null){
                        blockType.interact(event, block_offsetted.getX(), block_offsetted.getY(), block_offsetted.getZ(), block_offsetted.getWorld().getName());
                    }
                }
            }
        }
    }
}
