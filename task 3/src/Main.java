import java.util.*;
import java.util.concurrent.*;

public class Main {
    enum Part { HEAD, TORSO, HAND, FOOT }

    static class Factory implements Runnable {
        private final BlockingQueue<Part> storage;
        private final Random random = new Random();
        private final int days;

        Factory(BlockingQueue<Part> storage, int days) {
            this.storage = storage;
            this.days = days;
        }

        @Override
        public void run() {
            try {
                for (int d = 0; d < days; d++) {
                    int produced = random.nextInt(11); // 0..10 деталей
                    for (int i = 0; i < produced; i++) {
                        Part p = Part.values()[random.nextInt(Part.values().length)];
                        storage.put(p);
                    }
                    // день закончился
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Faction implements Runnable {
        private final String name;
        private final BlockingQueue<Part> storage;
        private final Map<Part, Integer> inventory = new EnumMap<>(Part.class);
        private final int days;

        Faction(String name, BlockingQueue<Part> storage, int days) {
            this.name = name;
            this.storage = storage;
            this.days = days;
            for (Part p : Part.values()) inventory.put(p, 0);
        }

        @Override
        public void run() {
            try {
                for (int d = 0; d < days; d++) {
                    // ночью можно взять до 5 деталей
                    for (int i = 0; i < 5; i++) {
                        Part p = storage.poll();
                        if (p != null) {
                            inventory.put(p, inventory.get(p) + 1);
                        }
                    }
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
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

        public String getName() { return name; }
    }

    public static void main(String[] args) throws InterruptedException {
        int days = 100;
        BlockingQueue<Part> storage = new LinkedBlockingQueue<>();
        Factory factory = new Factory(storage, days);
        Faction world = new Faction("World", storage, days);
        Faction wednesday = new Faction("Wednesday", storage, days);
        Thread tf = new Thread(factory);
        Thread tw = new Thread(world);
        Thread twed = new Thread(wednesday);
        tf.start();
        tw.start();
        twed.start();
        tf.join();
        tw.join();
        twed.join();
        int robotsWorld = world.robotsBuilt();
        int robotsWednesday = wednesday.robotsBuilt();
        System.out.println("World built: " + robotsWorld + " robots");
        System.out.println("Wednesday built: " + robotsWednesday + " robots");
        if (robotsWorld > robotsWednesday) System.out.println("Winner: World");
        else if (robotsWednesday > robotsWorld) System.out.println("Winner: Wednesday");
        else System.out.println("Draw!");
    }
}
