import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;
import java.util.OptionalDouble;

public final class Task {

    private Task() { }

    public static Set<String> uniqueCities(List<Order> orders) {
        if (orders == null) return Collections.emptySet();
        return orders.stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .map(Customer::getCity)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static double totalIncomeForCompletedOrders(List<Order> orders) {
        if (orders == null) return 0.0;
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Task::orderTotal)
                .sum();
    }

    public static Optional<String> mostPopularProductBySales(List<Order> orders) {
        if (orders == null) return Optional.empty();
        return orders.stream()
                .flatMap(o -> Optional.ofNullable(o.getItems()).map(List::stream).orElseGet(Stream::empty))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(OrderItem::getProductName, Collectors.summingInt(OrderItem::getQuantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public static OptionalDouble averageCheckForDeliveredOrders(List<Order> orders) {
        if (orders == null) return OptionalDouble.empty();
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Task::orderTotal)
                .average();
    }

    public static Set<Customer> customersWithMoreThanNOrders(List<Order> orders, long n) {
        if (orders == null) return Collections.emptySet();

        Map<String, Long> counts = orders.stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Customer::getCustomerId, Collectors.counting()));

        Set<String> ids = counts.entrySet().stream()
                .filter(e -> e.getValue() > n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return orders.stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .filter(c -> ids.contains(c.getCustomerId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static double orderTotal(Order order) {
        if (order == null || order.getItems() == null) return 0.0;
        return order.getItems().stream()
                .filter(Objects::nonNull)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

}
