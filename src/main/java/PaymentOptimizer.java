import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentOptimizer {

    public static Map<String, Map<String, BigDecimal>> optimizePayments(List<Order> orders, List<PaymentMethod> methods) {

        Map<String, PaymentMethod> methodMap = methods.stream()
                .collect(Collectors.toMap(PaymentMethod::getId, Function.identity()));

        orders.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<String, Map<String, BigDecimal>> allOrderCalculations = new LinkedHashMap<>();

        for (Order order : orders) {
            // This will store calculations for the current order
            Map<String, BigDecimal> orderCalculations = new LinkedHashMap<>();

            // 1. Calculate reductions for each eligible payment method
            if (order.getPromotions()!=null){
            for (String promotion : order.getPromotions()) {
                PaymentMethod method = methodMap.get(promotion);
                if (method != null && method.getLimit().compareTo(order.getValue()) >= 0) {
                    BigDecimal reduction = order.getValue().multiply(method.getDiscount().divide(BigDecimal.valueOf(100)));
                    orderCalculations.put(method.getId(), reduction);
                }
            }
            }
            // 2. Calculate PartialPoints reduction (20%)
            BigDecimal partialPointsReduction = order.getValue().multiply(BigDecimal.valueOf(0.1));
            orderCalculations.put("PartialPoints", partialPointsReduction);

            // 3. Calculate Points (PUNKTY) reduction if exists
            PaymentMethod punktyMethod = methodMap.get("PUNKTY");
            if (punktyMethod != null && punktyMethod.getLimit().compareTo(order.getValue()) >= 0) {
                BigDecimal pointsReduction = order.getValue().multiply(punktyMethod.getDiscount().divide(BigDecimal.valueOf(100)));
                orderCalculations.put("PUNKTY", pointsReduction);
            }

            // Sort the current order's calculations by reduction value (descending)
            Map<String, BigDecimal> sortedCalculations = orderCalculations.entrySet().stream()
                    .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
            allOrderCalculations.put(order.getId(), sortedCalculations);
        }
        return allOrderCalculations;







    }
}

