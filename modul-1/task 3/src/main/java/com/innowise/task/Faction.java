package com.innowise.task;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

public class Faction implements Runnable {
    private final String name;
    private final BlockingQueue<Part> storage;
    private final Map<Part, Integer> inventory = new EnumMap<>(Part.class);
    private final int days;
    private final CyclicBarrier dayBarrier;
    private final CyclicBarrier nightBarrier;

    public Faction(String name, BlockingQueue<Part> storage, int days,
                   CyclicBarrier dayBarrier, CyclicBarrier nightBarrier) {
        this.name = name;
        this.storage = storage;
        this.days = days;
        this.dayBarrier = dayBarrier;
        this.nightBarrier = nightBarrier;
        for (Part p : Part.values()) inventory.put(p, 0);
    }

    @Override
    public void run() {
        try {
            for (int d = 0; d < days; d++) {
                dayBarrier.await(); // ждём завершения дня

                for (int i = 0; i < 5; i++) {
                    Part p = storage.poll();
                    if (p != null) {
                        inventory.put(p, inventory.get(p) + 1);
                    }
                }

                nightBarrier.await();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public int robotsBuilt() {
        int heads = inventory.get(Part.HEAD);
        int torsos = inventory.get(Part.TORSO);
        int hands = inventory.get(Part.HAND) / 2;
        int feet = inventory.get(Part.FOOT) / 2;
        return Math.min(Math.min(heads, torsos), Math.min(hands, feet));
    }

    public String getName() {
        return name;
    }
}
