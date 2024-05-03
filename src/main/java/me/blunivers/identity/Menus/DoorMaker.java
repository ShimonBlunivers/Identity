package me.blunivers.identity.Menus;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.StringJoiner;

import static org.bukkit.Bukkit.getServer;

public class DoorMaker implements Listener {
    private final String menuName = "Výběr Identity Bloků";

    Player player;
    BlockInstance blockInstance;

    @EventHandler
    public void build(SignChangeEvent event){
        if (event.getPlayer().getUniqueId() == player.getUniqueId()){
            StringJoiner metadata = new StringJoiner(",");

            for (Component component : event.lines()){
                if (!((TextComponent) component).content().isEmpty()) metadata.add(((TextComponent) component).content());
            }
            finish(metadata.toString().replace(" ", ""));

            event.getBlock().setType(Material.AIR);
        }
    }

    public DoorMaker(Player player, BlockInstance blockInstance){
        this.player = player;
        this.blockInstance = blockInstance;
        getServer().getPluginManager().registerEvents(this, Identity.instance);

        Block block = player.getWorld().getBlockAt(player.getLocation());

        if (!(block.getState() instanceof Sign)){
            block.setType(Material.OAK_SIGN, false);
        }
        Sign sign = (Sign) block.getState();
        player.openSign(sign);
        sign.setWaxed(true);


//        int invSize = 9 + (BlockType.get().values().size() / 9) * 9;
//        Inventory inventory = Bukkit.createInventory(player, invSize, menuName);
//
//        int i = 0;
//        for (BlockType blockType : BlockType.get().values()) {
//            inventory.setItem(i++, EnvironmentManager.getCustomItemWithoutLabel(blockType));
//        }
//
//
//        player.openInventory(inventory);
//
//        return true;

    }


    public void finish(String metadata){
        Identity.database.environment_addMetadataToBlock(blockInstance.x, blockInstance.y, blockInstance.z, blockInstance.world.getName(), metadata);
        HandlerList.unregisterAll(this);
    }
}
