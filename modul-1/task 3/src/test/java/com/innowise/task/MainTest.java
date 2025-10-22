package com.innowise.task;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void factionBuildsRobotsCorrectly() {
        BlockingQueue<Part> dummyStorage = new LinkedBlockingQueue<>();
        CyclicBarrier dummyBarrier = new CyclicBarrier(1);
        Faction faction = new Faction("TestFaction", dummyStorage, 0, dummyBarrier, dummyBarrier);

        Map<Part, Integer> inventory = new EnumMap<>(Part.class);
        inventory.put(Part.HEAD, 1);
        inventory.put(Part.TORSO, 1);
        inventory.put(Part.HAND, 2);
        inventory.put(Part.FOOT, 2);

        try {
            var field = Faction.class.getDeclaredField("inventory");
            field.setAccessible(true);
            field.set(faction, inventory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, faction.robotsBuilt());
    }
}
