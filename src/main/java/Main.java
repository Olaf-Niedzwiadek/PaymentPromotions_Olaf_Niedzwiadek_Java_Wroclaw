import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Użycie: java -jar app.jar <orders.json> <paymentmethods.json>");
            System.exit(1);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        List<Order> orders = objectMapper.readValue(
                new File(args[0]),
                new TypeReference<List<Order>>() {}
        );

        List<PaymentMethod> methods = objectMapper.readValue(
                new File(args[1]),
                new TypeReference<List<PaymentMethod>>() {}
        );

        System.out.println("Wczytano " + orders.size() + " zamówień i " + methods.size() + " metod płatności.");

    }
}
