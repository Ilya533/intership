package task;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

public class Factory implements Runnable {
    private final BlockingQueue<Part> storage;
    private final Random random = new Random();
    private final int days;
    private final CyclicBarrier dayBarrier;
    private final CyclicBarrier nightBarrier;

    public Factory(BlockingQueue<Part> storage, int days,
                   CyclicBarrier dayBarrier, CyclicBarrier nightBarrier) {
        this.storage = storage;
        this.days = days;
        this.dayBarrier = dayBarrier;
        this.nightBarrier = nightBarrier;
    }

    @Override
    public void run() {
        try {
            for (int d = 0; d < days; d++) {
                int produced = random.nextInt(11);
                for (int i = 0; i < produced; i++) {
                    Part p = Part.values()[random.nextInt(Part.values().length)];
                    storage.put(p);
                }

                dayBarrier.await();
                nightBarrier.await();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
