package com.innowise.task;

import java.util.*;
import java.util.stream.Collectors;

public final class Task {

    private Task() { }

    public static Set<String> uniqueCities(List<Order> orders) {
        return Optional.ofNullable(orders)
                .orElse(Collections.emptyList())
                .stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .map(Customer::getCity)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static double totalIncomeForCompletedOrders(List<Order> orders) {
        return Optional.ofNullable(orders)
                .orElse(Collections.emptyList())
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Task::orderTotal)
                .sum();
    }

    public static Optional<String> mostPopularProductBySales(List<Order> orders) {
        return Optional.ofNullable(orders)
                .orElse(Collections.emptyList())
                .stream()
                .map(Order::getItems)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        Collectors.summingInt(OrderItem::getQuantity)
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public static OptionalDouble averageCheckForDeliveredOrders(List<Order> orders) {
        return Optional.ofNullable(orders)
                .orElse(Collections.emptyList())
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Task::orderTotal)
                .average();
    }

    public static Set<Customer> customersWithMoreThanNOrders(List<Order> orders, long n) {
        if (orders == null) return Collections.emptySet();

        Map<String, Long> orderCounts = orders.stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Customer::getCustomerId,
                        Collectors.counting()
                ));

        Map<String, Customer> customerMap = orders.stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Customer::getCustomerId,
                        customer -> customer,
                        (existing, replacement) -> existing
                ));

        return orderCounts.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > n)
                .map(Map.Entry::getKey)
                .map(customerMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static double orderTotal(Order order) {
        if (order == null || order.getItems() == null) return 0.0;
        return order.getItems()
                .stream()
                .filter(Objects::nonNull)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}