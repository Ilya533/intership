package task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int days = 100;
        BlockingQueue<Part> storage = new LinkedBlockingQueue<>();

        CyclicBarrier dayBarrier = new CyclicBarrier(3);
        CyclicBarrier nightBarrier = new CyclicBarrier(3);

        Factory factory = new Factory(storage, days, dayBarrier, nightBarrier);
        Faction world = new Faction("World", storage, days, dayBarrier, nightBarrier);
        Faction wednesday = new Faction("Wednesday", storage, days, dayBarrier, nightBarrier);

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
