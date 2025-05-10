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

        ObjectMapper objectMapper = new ObjectMapper();    //engine for JSON -> java conversion

        List<Order> orders = objectMapper.readValue(      //readup of list of orders based on passed JSON file
                new File(ordersPath),
                new TypeReference<List<Order>>() {}   //json is a list of objects of type Order
        );

        List<PaymentMethod> methods = objectMapper.readValue( //readup of list of payment methods based on passed JSON file
                new File(methodsPath),
                new TypeReference<List<PaymentMethod>>() {}
        );
        //testing -----------------------------------------------------------------------------------------------------------------------------
        Map<String, Map<String, BigDecimal>> results = PaymentOptimizer.optimizePayments(orders, methods);

        for (String orderId : results.keySet()) {
            System.out.println("\nOrder: " + orderId);
            Map<String, BigDecimal> reductions = results.get(orderId);
            reductions.forEach((method, reduction) ->
                    System.out.printf("- %s: %.2f%n", method, reduction));
        }
    }
}
