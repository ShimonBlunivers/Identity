package me.blunivers.identity.Health.Conditions.Illnesses;

import org.bukkit.entity.Player;

public class Tetanus extends Illness {
    public Tetanus() {
        super("Tetanus","Tetanus", 40, 7);
        symptomsTimer = 20;
    }

    @Override
    public void symptoms(Player player) {
        player.chat("*tetanus noises*");
    }
}
