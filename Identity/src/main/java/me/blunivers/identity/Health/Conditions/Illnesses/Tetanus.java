package me.blunivers.identity.Health.Conditions.Illnesses;

import org.bukkit.entity.Player;

public class Tetanus extends Illness {
    public Tetanus() {
        super("tetanus","Tetanus", 100); //500
        symptomsChance = 40;
    }

    @Override
    public void symptoms(Player player) {
        player.chat("*tetanus noises*");
    }
}
