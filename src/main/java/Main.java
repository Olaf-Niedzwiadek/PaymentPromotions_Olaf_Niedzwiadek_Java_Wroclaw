import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
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

        Map<String, BigDecimal> optimizationResults = PaymentOptimizer.optimizePayments(orders, methods);
        PaymentOptimizer.printFundsUsed(optimizationResults);

    }
}


