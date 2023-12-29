package me.blunivers.identity.Jobs;


import org.bukkit.entity.Player;

import java.util.ArrayList;

public class OccupationRegistry {
    public Player player;
    public ArrayList<Occupation> occupations = new ArrayList<>();

    public int jobLimit = 2;

    public OccupationRegistry(Player _employee) {
        player = _employee;
    }

    public void addOccupation(Occupation occupation) {
        if (occupations.size() < jobLimit) occupations.add(occupation);
    }

    @Override
    public String toString() {
        return "Employee: " + player.getDisplayName() + ", " + occupations.toString();
    }


}
