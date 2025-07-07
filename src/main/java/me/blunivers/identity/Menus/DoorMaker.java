package me.blunivers.identity.Menus;

import me.blunivers.identity.Environment.BlockInstance;
import me.blunivers.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
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
    public void build(SignChangeEvent event) {
        if (event.getPlayer().getUniqueId() == player.getUniqueId()) {
            StringJoiner metadata = new StringJoiner(",");

            for (Component component : event.lines()) {
                if (!((TextComponent) component).content().isEmpty())
                    metadata.add(((TextComponent) component).content());
            }
            finish(metadata.toString().replace(" ", ""));
            event.getBlock().setType(Material.AIR);
        }
    }

    public DoorMaker(Player player, BlockInstance blockInstance) {
        this.player = player;
        this.blockInstance = blockInstance;
        getServer().getPluginManager().registerEvents(this, Identity.instance);

        Block block = player.getWorld().getBlockAt(player.getLocation());

        Block freeBlockFinder = block;
        for (int i = 0; i < 42; i++) {
            if (freeBlockFinder.getType() == Material.AIR)
                break;
            if (i <= 14) {
                freeBlockFinder = block.getWorld().getBlockAt(block.getX(), block.getY() + i - 7, block.getZ());
            } else if (i <= 28) {
                freeBlockFinder = block.getWorld().getBlockAt(block.getX() + i - 7 - 14, block.getY(), block.getZ());
            } else {
                freeBlockFinder = block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + i - 7 - 28);
            }
        }
        block = freeBlockFinder;

        block.setType(Material.OAK_SIGN, false);
        Sign sign = (Sign) block.getState();
        player.openSign(sign, Side.FRONT);
    }

    public void finish(String metadata) {
        HandlerList.unregisterAll(this);

        if (blockInstance.blockType.verifyMetadata(metadata)) {
            Identity.database.environment_addMetadataToBlock(blockInstance.x, blockInstance.y, blockInstance.z,
                    blockInstance.world.getName(), metadata);
        } else {
            Identity.database.environment_removeCustomBlock(blockInstance.x, blockInstance.y, blockInstance.z,
                    blockInstance.world.getName());
            blockInstance.block.setType(Material.AIR, false);
            blockInstance.offsetted_block.setType(Material.AIR);
            player.sendMessage(Component.text(
                    "Neplatný zápis práv, práva napište na cedulku jeden zápis na jeden řádek ve formátu -> JménoProfese:PotřebnýLevelVProfesi")
                    .color(NamedTextColor.RED));
        }
    }
}
