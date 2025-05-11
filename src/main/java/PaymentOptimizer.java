import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentOptimizer {

    public static Map<String, BigDecimal> optimizePayments(List<Order> orders, List<PaymentMethod> methods) {
        Map<String, PaymentMethod> methodMap = buildMethodMap(methods);
        orders.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<String, Map<String, BigDecimal>> allOrderCalculations = calculateOrderReductions(orders, methodMap);

        Map<String, BigDecimal> initialBalances = initializeBalances(methods);
        Map<String, BigDecimal> methodBalances = new HashMap<>(initialBalances);
        Map<String, BigDecimal> fundsUsed = initialBalances.keySet().stream()
                .collect(Collectors.toMap(k -> k, k -> BigDecimal.ZERO));

        processPaymentsForOrders(orders, allOrderCalculations, methodBalances, fundsUsed);

        return fundsUsed;
    }
    public static void printFundsUsed(Map<String, BigDecimal> fundsUsed) {
        fundsUsed.forEach((method, amount) ->
                System.out.printf("%s: %.2f%n", method, amount));
    }


    private static Map<String, PaymentMethod> buildMethodMap(List<PaymentMethod> methods) {
        return methods.stream().collect(Collectors.toMap(PaymentMethod::getId, Function.identity()));
    }

    private static Map<String, BigDecimal> initializeBalances(List<PaymentMethod> methods) {
        return methods.stream().collect(Collectors.toMap(PaymentMethod::getId, PaymentMethod::getLimit));
    }

    static Map<String, Map<String, BigDecimal>> calculateOrderReductions(List<Order> orders, Map<String, PaymentMethod> methodMap) {
        Map<String, Map<String, BigDecimal>> allOrderCalculations = new LinkedHashMap<>();

        for (Order order : orders) {
            Map<String, BigDecimal> reductions = new LinkedHashMap<>();

            if (order.getPromotions() != null) {
                for (String promo : order.getPromotions()) {
                    PaymentMethod method = methodMap.get(promo);
                    if (method != null && method.getLimit().compareTo(order.getValue()) >= 0) {
                        reductions.put(method.getId(),
                                order.getValue().multiply(method.getDiscount().divide(BigDecimal.valueOf(100))));
                    }
                }
            }
            reductions.put("PartialPoints", order.getValue().multiply(BigDecimal.valueOf(0.1)));
            if (methodMap.containsKey("PUNKTY")) {
                PaymentMethod punkty = methodMap.get("PUNKTY");
                if (punkty.getLimit().compareTo(order.getValue()) >= 0) {
                    reductions.put("PUNKTY", order.getValue().multiply(
                            punkty.getDiscount().divide(BigDecimal.valueOf(100))));
                }
            }
            allOrderCalculations.put(order.getId(), sortReductions(reductions));
        }
        return allOrderCalculations;
    }

    static Map<String, BigDecimal> sortReductions(Map<String, BigDecimal> reductions) {
        return reductions.entrySet().stream()
                .sorted((e1, e2) -> {
                    int cmp = e2.getValue().compareTo(e1.getValue());
                    if (cmp != 0) return cmp;

                    boolean e1Preferred = e1.getKey().equals("PartialPoints") || e1.getKey().equals("PUNKTY");
                    boolean e2Preferred = e2.getKey().equals("PartialPoints") || e2.getKey().equals("PUNKTY");
                    if (e1Preferred && !e2Preferred) return -1;
                    if (!e1Preferred && e2Preferred) return 1;

                    return e1.getKey().compareTo(e2.getKey());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    static void processPaymentsForOrders(List<Order> orders,
                                                 Map<String, Map<String, BigDecimal>> allOrderCalculations,
                                                 Map<String, BigDecimal> methodBalances,
                                                 Map<String, BigDecimal> fundsUsed) {

        for (Order order : orders) {
            processSingleOrderPayment(order, allOrderCalculations.get(order.getId()), methodBalances, fundsUsed);
        }
    }

     static void processSingleOrderPayment(Order order,
                                                  Map<String, BigDecimal> reductions,
                                                  Map<String, BigDecimal> methodBalances,
                                                  Map<String, BigDecimal> fundsUsed) {

        BigDecimal remaining = order.getValue();

        for (Map.Entry<String, BigDecimal> entry : reductions.entrySet()) {
            String methodId = entry.getKey();
            BigDecimal reduction = entry.getValue();

            if (methodId.equals("PartialPoints")) {
                if (methodBalances.containsKey("PUNKTY") &&
                        methodBalances.get("PUNKTY").compareTo(reduction) >= 0) {

                    methodBalances.put("PUNKTY", methodBalances.get("PUNKTY").subtract(reduction));
                    fundsUsed.put("PUNKTY", fundsUsed.get("PUNKTY").add(reduction));
                    BigDecimal toPay = remaining.subtract(reduction.multiply(BigDecimal.valueOf(2)));

                    if (toPay.compareTo(BigDecimal.ZERO) > 0) {
                        payWithHighestBalanceMethod(toPay, methodBalances, fundsUsed, "PUNKTY");
                    }
                    return;
                }
            } else if (methodBalances.containsKey(methodId)) {
                BigDecimal toPay = remaining.subtract(reduction);
                if (toPay.compareTo(BigDecimal.ZERO) >= 0 &&
                        methodBalances.get(methodId).compareTo(toPay) >= 0) {
                    methodBalances.put(methodId, methodBalances.get(methodId).subtract(toPay));
                    fundsUsed.put(methodId, fundsUsed.get(methodId).add(toPay));
                    return;
                }
            }
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            payWithHighestBalanceMethod(remaining, methodBalances, fundsUsed, null);
        }
    }

     static void payWithHighestBalanceMethod(BigDecimal amount,
                                                    Map<String, BigDecimal> balances,
                                                    Map<String, BigDecimal> fundsUsed,
                                                    String excludeMethod) {

        List<Map.Entry<String, BigDecimal>> available = balances.entrySet().stream()
                .filter(e -> !e.getKey().equals(excludeMethod))
                .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();

        for (Map.Entry<String, BigDecimal> method : available) {
            BigDecimal payment = amount.min(method.getValue());
            balances.put(method.getKey(), method.getValue().subtract(payment));
            fundsUsed.merge(method.getKey(), payment, BigDecimal::add);
            amount = amount.subtract(payment);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) return;
        }
    }
}



