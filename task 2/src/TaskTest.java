import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private OrderItem item(String name, int qty, double price, Category cat) {
        OrderItem it = new OrderItem();
        it.setProductName(name);
        it.setQuantity(qty);
        it.setPrice(price);
        it.setCategory(cat);
        return it;
    }

    private Customer customer(String id, String name, String city) {
        Customer c = new Customer();
        c.setCustomerId(id);
        c.setName(name);
        c.setCity(city);
        c.setEmail(name + "@example.com");
        c.setRegisteredAt(LocalDateTime.now().minusDays(10));
        c.setAge(30);
        return c;
    }

    private Order order(String id, Customer c, OrderStatus status, OrderItem... items) {
        Order o = new Order();
        o.setOrderId(id);
        o.setCustomer(c);
        o.setOrderDate(LocalDateTime.now());
        o.setStatus(status);
        o.setItems(Arrays.asList(items));
        return o;
    }

    @Test
    void testUniqueCities() {
        Customer c1 = customer("C1", "Alice", "Minsk");
        Customer c2 = customer("C2", "Bob", "Gomel");
        Customer c3 = customer("C3", "Carl", "Minsk");

        Order o1 = order("O1", c1, OrderStatus.NEW);
        Order o2 = order("O2", c2, OrderStatus.NEW);
        Order o3 = order("O3", c3, OrderStatus.NEW);

        Set<String> cities = Task.uniqueCities(Arrays.asList(o1, o2, o3));
        assertEquals(2, cities.size());
        assertTrue(cities.contains("Minsk"));
        assertTrue(cities.contains("Gomel"));
    }

    @Test
    void testTotalIncomeForCompletedOrders() {
        Customer c = customer("C1", "Alice", "Minsk");
        OrderItem it1 = item("Book", 2, 10.0, Category.BOOKS);
        OrderItem it2 = item("Pen", 1, 2.5, Category.BOOKS);

        Order delivered = order("O1", c, OrderStatus.DELIVERED, it1, it2);
        Order cancelled = order("O2", c, OrderStatus.CANCELLED, item("Phone", 1, 200.0, Category.ELECTRONICS));

        double total = Task.totalIncomeForCompletedOrders(Arrays.asList(delivered, cancelled));
        assertEquals(22.5, total, 1e-9);
    }

    @Test
    void testMostPopularProductBySales() {
        Customer c = customer("C1", "Alice", "Minsk");
        OrderItem a1 = item("iPhone", 1, 500.0, Category.ELECTRONICS);
        OrderItem a2 = item("iPhone", 2, 500.0, Category.ELECTRONICS);
        OrderItem b1 = item("Book", 5, 10.0, Category.BOOKS);

        Order o1 = order("O1", c, OrderStatus.DELIVERED, a1);
        Order o2 = order("O2", c, OrderStatus.DELIVERED, a2);
        Order o3 = order("O3", c, OrderStatus.DELIVERED, b1);

        Optional<String> popular = Task.mostPopularProductBySales(Arrays.asList(o1, o2, o3));
        assertTrue(popular.isPresent());
        assertEquals("Book", popular.get());
    }

    @Test
    void testAverageCheckForDeliveredOrders() {
        Customer c = customer("C1", "Alice", "Minsk");
        OrderItem it1 = item("A", 1, 100.0, Category.ELECTRONICS);
        OrderItem it2 = item("B", 2, 50.0, Category.HOME);

        Order d1 = order("O1", c, OrderStatus.DELIVERED, it1);
        Order d2 = order("O2", c, OrderStatus.DELIVERED, it2);
        Order newOrder = order("O3", c, OrderStatus.NEW, item("C", 1, 100.0, Category.TOYS));

        OptionalDouble avg = Task.averageCheckForDeliveredOrders(Arrays.asList(d1, d2, newOrder));
        assertTrue(avg.isPresent());
        assertEquals(100.0, avg.getAsDouble(), 1e-9);
    }

    @Test
    void testCustomersWithMoreThanFiveOrders() {
        Customer c1 = customer("C1", "Alice", "Minsk");
        Customer c2 = customer("C2", "Bob", "Grodno");
        Customer c3 = customer("C3", "Carol", "Vitebsk");

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 6; i++) orders.add(order("A" + i, c1, OrderStatus.NEW));
        for (int i = 0; i < 5; i++) orders.add(order("B" + i, c2, OrderStatus.NEW));
        for (int i = 0; i < 7; i++) orders.add(order("C" + i, c3, OrderStatus.NEW));

        Set<Customer> result = Task.customersWithMoreThanNOrders(orders, 5);
        assertEquals(2, result.size());
        Set<String> ids = new HashSet<>();
        result.forEach(c -> ids.add(c.getCustomerId()));
        assertTrue(ids.contains("C1"));
        assertTrue(ids.contains("C3"));
        assertFalse(ids.contains("C2"));
    }

    @Test
    void testOrderTotalHelper() {
        OrderItem i1 = item("X", 2, 12.5, Category.HOME);
        OrderItem i2 = item("Y", 1, 5.0, Category.BEAUTY);
        Order o = order("T1", customer("C1", "Name", "City"), OrderStatus.DELIVERED, i1, i2);
        assertEquals(30.0, Task.orderTotal(o), 1e-9);
    }
}
