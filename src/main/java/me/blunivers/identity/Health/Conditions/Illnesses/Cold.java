package me.blunivers.identity.Health.Conditions.Illnesses;

import me.blunivers.identity.Health.Conditions.ConditionInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

public class Cold extends Illness {
    public Cold() {
        super("cold", "Rýma", 1000);
        symptomsChance = 10;
    }
    @Override
    public void symptoms(Player player, ConditionInstance conditionInstance) {
        Random random = new Random();

        Location playerLocation = player.getLocation();

        Location eyesLocation = player.getEyeLocation();

//        player.chat(playerLocation.getYaw() + ", " + playerLocation.getPitch());

        float yaw = playerLocation.getYaw();       // Do stran

        float speed = 0.75f;

        float vectorX = - (float) Math.sin(Math.toRadians(yaw)) * speed;
        float vectorZ = (float) Math.cos(Math.toRadians(yaw)) * speed;


        player.getWorld().playSound(eyesLocation, Sound.ENTITY_PANDA_SNEEZE, 3.0f, (float) (0.1 + ((double) random.nextInt(10) / 10)));


//        player.chat("Strana: " + yaw);
//        player.chat( "X: " + ((float)Math.round(vectorX * 1000) / 1000) + " ; Z: " + ((float)Math.round(vectorZ * 1000) / 1000));


        for (int i = 0; i < 5; i++)
            player.getWorld().spawnParticle(Particle.CLOUD, eyesLocation.getX(), eyesLocation.getY(), eyesLocation.getZ(), 0, vectorX + player.getVelocity().getX(), player.getVelocity().getY(), vectorZ + player.getVelocity().getZ(), 0.5);


        int sneezeFactor = (int) (- (20 * conditionInstance.getSymptomMultiplier()) - random.nextInt((int)(20 * conditionInstance.getSymptomMultiplier()))); // 30

        playerLocation.setPitch(Math.min(Math.max(playerLocation.getPitch() - sneezeFactor, -90), 90));
        player.teleport(playerLocation);
        player.setVelocity(player.getVelocity());
    }
}
