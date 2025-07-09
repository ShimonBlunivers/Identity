package me.blunivers.identity.Environment;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import java.util.Set;
import me.blunivers.identity.Identity;
import me.blunivers.identity.Manager;
import me.blunivers.identity.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
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
import org.reflections.Reflections;

public class EnvironmentManager extends Manager implements Runnable, Listener {
    public static final EnvironmentManager singleton = new EnvironmentManager();

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

    public static void givePlayerIdentityStick(Player player) {
        ItemStack item = new ItemStack(Material.STICK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("IdentityStick" + Identity.identificator));
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

        player.sendMessage(Component.text("You've been given the mighty Identity Stick!", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
            return;
        }
        Block block = event.getBlock();
        Identity.database.environment_removeCustomBlock(block.getX(), block.getY(), block.getZ(),
                block.getWorld().getName());
    }

    @EventHandler
    public void onDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        Identity.database.environment_removeCustomBlock(block.getX(), block.getY(), block.getZ(),
                block.getWorld().getName());
    }

    public BlockInstance getCustomBlock(Block block) {
        int blockX = block.getX();
        int blockY = block.getY();
        int blockZ = block.getZ();
        for (BlockInstance customBlock : Identity.database.environment_getCustomBlockInstances()) {
            if (blockX == customBlock.position.x && blockY == customBlock.position.y
                    && blockZ == customBlock.position.z) {
                return customBlock;
            }
        }
        return null;
    }

    @EventHandler
    public void useIdentityStick(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getClickedBlock() == null || !event.getPlayer().isOp()) {
            return;
        }
        ItemStack itemStack = event.getItem();
        if (itemStack.getType().equals(Material.STICK)) {
            return;
        }
        if (!(Utility.componentToString(itemStack.getItemMeta().displayName())
                .equals("IdentityStick" + Identity.identificator))) {
            return;
        }
        event.setCancelled(true);

        String identityStickString = "<IdentityStick> ";

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        BlockInstance customBlock = getCustomBlock(block);
        if (customBlock == null) {
            player.sendMessage(
                    Component.text(identityStickString, NamedTextColor.LIGHT_PURPLE)
                            .append(Component.text("Vanilla block", NamedTextColor.RED)));
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            player.getInventory().addItem(getCustomItem(customBlock.blockType));
            player.sendMessage(Component.text(identityStickString, NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text("A block was added into your inventory:", NamedTextColor.WHITE))
                    .append(Component.text(" " + customBlock.blockType.name, NamedTextColor.GREEN)));
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            player.sendMessage(Component.text(identityStickString, NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(customBlock.blockType.name, NamedTextColor.GREEN)));
        }
    }

    @EventHandler
    public void invisibleItemFrameProtection(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType() != EntityType.ITEM_FRAME) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) entity;
        if (itemFrame.isVisible()) {
            return;
        }
        if (itemFrame.getItem().equals(new ItemStack(Material.AIR, 1))) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp()) {
            return;
        }
        ItemMeta meta = event.getItemInHand().getItemMeta();
        Block block = event.getBlock();

        if (!Utility.componentToString(meta.displayName()).contains(Identity.identificator)) {
            return;
        }
        BlockType blockType = BlockType
                .get(Utility.componentToString(meta.displayName()).replace(Identity.identificator, ""));
        if (blockType == null) {
            return;
        }
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
            return;
        }
        blockType.place(event.getPlayer(), block.getX(), block.getY(), block.getZ(),
                block.getWorld().getName());
    }

    public static ItemStack getCustomItem(BlockType blockType) {
        ItemStack item = new ItemStack(blockType.material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(blockType.name + Identity.identificator));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getCustomItemWithoutLabel(BlockType blockType) {
        ItemStack item = new ItemStack(blockType.material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(blockType.name));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void run() {
        updateBlockInstances();
    }

    public void updateBlockInstances() {
        for (BlockType blockType : BlockType.get().values()) {
            blockType.update();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        for (BlockType blockType : BlockType.get().values()) {
            Block block = event.getClickedBlock();
            if (block == null || block.getType() != blockType.material) {
                break;
            }
            blockType.interact(event, block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
            if (blockType.offset.x == 0 && blockType.offset.y == 0 && blockType.offset.z == 0) {
                break;
            }
            Block block_offsetted = event.getPlayer().getWorld()
                    .getBlockAt(new Location(event.getPlayer().getWorld(), block.getX() + blockType.offset.x,
                            block.getY() + blockType.offset.y, block.getZ() + blockType.offset.z));
            if (block_offsetted != null) {
                blockType.interact(event, block_offsetted.getX(), block_offsetted.getY(),
                        block_offsetted.getZ(), block_offsetted.getWorld().getName());
            }
        }
    }
}
