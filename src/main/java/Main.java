import com.fasterxml.jackson.core.type.TypeReference; //for json java conversion
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;  //for file readup list of desired objects creation
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        String ordersPath = "C:\\Users\\HP\\OneDrive\\Pulpit\\internship_task\\orders.json";
        String methodsPath = "C:\\Users\\HP\\OneDrive\\Pulpit\\internship_task\\paymentmethods.json";

        ObjectMapper objectMapper = new ObjectMapper();

        List<Order> orders = objectMapper.readValue(
                new File(ordersPath),
                new TypeReference<List<Order>>() {}
        );

        List<PaymentMethod> methods = objectMapper.readValue(
                new File(methodsPath),
                new TypeReference<List<PaymentMethod>>() {}
        );

        // Get all results from the optimizer
        Map<String, Object> optimizationResults = PaymentOptimizer.optimizePayments(orders, methods);

        // Extract just the calculations if that's what you want to display
        @SuppressWarnings("unchecked")
        Map<String, Map<String, BigDecimal>> calculations =
                (Map<String, Map<String, BigDecimal>>) optimizationResults.get("calculations");

        // Display the calculations (original code)
        for (String orderId : calculations.keySet()) {
            System.out.println("\nOrder: " + orderId);
            Map<String, BigDecimal> reductions = calculations.get(orderId);
            reductions.forEach((method, reduction) ->
                    System.out.printf("- %s: %.2f%n", method, reduction));
        }

        // You can also access the other results if needed:
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> fundsUsed =
                (Map<String, BigDecimal>) optimizationResults.get("funds_used");

        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> remainingBalances =
                (Map<String, BigDecimal>) optimizationResults.get("remaining_balances");

        System.out.println("\nFunds Used:");
        fundsUsed.forEach((method, amount) ->
                System.out.printf("- %s: %.2f%n", method, amount));

        System.out.println("\nRemaining Balances:");
        remainingBalances.forEach((method, balance) ->
                System.out.printf("- %s: %.2f%n", method, balance));
    }
}
