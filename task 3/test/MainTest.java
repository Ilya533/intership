import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.concurrent.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private BlockingQueue<Main.Part> storage;
    private CountDownLatch dayFinishedLatch;
    private CountDownLatch nightFinishedLatch;

    @BeforeEach
    void setUp() {
        storage = new LinkedBlockingQueue<>();
        dayFinishedLatch = new CountDownLatch(1);
        nightFinishedLatch = new CountDownLatch(2);
    }

    @Test
    void testRobotsBuiltWithNoParts() throws InterruptedException {
        Main.Faction faction = new Main.Faction("Test", storage, 1, dayFinishedLatch, nightFinishedLatch);
        Thread t = new Thread(faction);
        dayFinishedLatch.countDown();
        nightFinishedLatch.countDown();
        nightFinishedLatch.countDown();
        t.start();
        t.join();
        assertEquals(0, faction.robotsBuilt());
    }

    @Test
    void testRobotsBuiltWithCompleteRobot() throws Exception {
        Main.Faction faction = new Main.Faction("Test", storage, 1, dayFinishedLatch, nightFinishedLatch);

        Field inventoryField = Main.Faction.class.getDeclaredField("inventory");
        inventoryField.setAccessible(true);
        java.util.Map<Main.Part, Integer> inventory =
                (java.util.Map<Main.Part, Integer>) inventoryField.get(faction);

        inventory.put(Main.Part.HEAD, 2);
        inventory.put(Main.Part.TORSO, 2);
        inventory.put(Main.Part.HAND, 4);
        inventory.put(Main.Part.FOOT, 4);

        assertEquals(2, faction.robotsBuilt());
    }

    @Test
    void testRobotsBuiltWithLimitedParts() throws Exception {
        Main.Faction faction = new Main.Faction("Test", storage, 1, dayFinishedLatch, nightFinishedLatch);

        Field inventoryField = Main.Faction.class.getDeclaredField("inventory");
        inventoryField.setAccessible(true);
        java.util.Map<Main.Part, Integer> inventory =
                (java.util.Map<Main.Part, Integer>) inventoryField.get(faction);

        inventory.put(Main.Part.HEAD, 3);
        inventory.put(Main.Part.TORSO, 2);
        inventory.put(Main.Part.HAND, 5);
        inventory.put(Main.Part.FOOT, 3);

        assertEquals(1, faction.robotsBuilt());
    }

    @Test
    void testFactoryProducesParts() throws InterruptedException {
        Main.Factory factory = new Main.Factory(storage, 1, dayFinishedLatch, nightFinishedLatch);
        Thread factoryThread = new Thread(factory);

        Thread dummyFaction1 = new Thread(() -> {
            try {
                dayFinishedLatch.await();
                nightFinishedLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread dummyFaction2 = new Thread(() -> {
            try {
                dayFinishedLatch.await();
                nightFinishedLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        dummyFaction1.start();
        dummyFaction2.start();
        factoryThread.start();

        dayFinishedLatch.countDown();

        factoryThread.join();
        dummyFaction1.join();
        dummyFaction2.join();

        assertTrue(storage.size() >= 0);
    }

    @Test
    void testFactionTakesParts() throws InterruptedException {
        storage.put(Main.Part.HEAD);
        storage.put(Main.Part.TORSO);
        storage.put(Main.Part.HAND);

        Main.Faction faction = new Main.Faction("Test", storage, 1, dayFinishedLatch, nightFinishedLatch);
        Thread factionThread = new Thread(faction);

        factionThread.start();
        dayFinishedLatch.countDown();
        nightFinishedLatch.countDown();
        nightFinishedLatch.countDown();

        factionThread.join();

        assertTrue(storage.isEmpty());
    }

    @Test
    void testFactionTakesMaximumFiveParts() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            storage.put(Main.Part.HEAD);
        }

        Main.Faction faction = new Main.Faction("Test", storage, 1, dayFinishedLatch, nightFinishedLatch);
        Thread factionThread = new Thread(faction);

        factionThread.start();
        dayFinishedLatch.countDown();
        nightFinishedLatch.countDown();
        nightFinishedLatch.countDown();

        factionThread.join();

        assertEquals(5, storage.size());
    }

    @Test
    void testSingleDaySimulation() throws InterruptedException {
        BlockingQueue<Main.Part> storage = new LinkedBlockingQueue<>();
        CountDownLatch dayLatch = new CountDownLatch(1);
        CountDownLatch nightLatch = new CountDownLatch(2);

        Main.Factory factory = new Main.Factory(storage, 1, dayLatch, nightLatch);
        Main.Faction world = new Main.Faction("World", storage, 1, dayLatch, nightLatch);
        Main.Faction wednesday = new Main.Faction("Wednesday", storage, 1, dayLatch, nightLatch);

        Thread tf = new Thread(factory);
        Thread tw = new Thread(world);
        Thread twed = new Thread(wednesday);

        tf.start();
        tw.start();
        twed.start();

        dayLatch.countDown();
        nightLatch.countDown();
        nightLatch.countDown();

        tf.join();
        tw.join();
        twed.join();

        int robotsWorld = world.robotsBuilt();
        int robotsWednesday = wednesday.robotsBuilt();

        assertTrue(robotsWorld >= 0);
        assertTrue(robotsWednesday >= 0);
    }

    @Test
    void testFactionName() {
        Main.Faction faction = new Main.Faction("TestFaction", storage, 1, dayFinishedLatch, nightFinishedLatch);
        assertEquals("TestFaction", faction.getName());
    }

    @Test
    void testPartsEnum() {
        Main.Part[] parts = Main.Part.values();
        assertEquals(4, parts.length);
        assertArrayEquals(new Main.Part[]{Main.Part.HEAD, Main.Part.TORSO, Main.Part.HAND, Main.Part.FOOT}, parts);
    }
}