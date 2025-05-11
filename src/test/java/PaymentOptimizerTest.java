import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentOptimizerTest {

    @Test
    void testPartialPointsUsedCorrectly() {
        Order order = new Order("ORDER1", BigDecimal.valueOf(100), List.of("mZysk"));

        Map<String, BigDecimal> reductions = Map.of("PartialPoints", BigDecimal.valueOf(10));
        Map<String, BigDecimal> methodBalances = new HashMap<>();
        methodBalances.put("PUNKTY", BigDecimal.valueOf(20));
        methodBalances.put("mZysk", BigDecimal.valueOf(100));

        Map<String, BigDecimal> fundsUsed = new HashMap<>();
        fundsUsed.put("PUNKTY", BigDecimal.ZERO);
        fundsUsed.put("mZysk", BigDecimal.ZERO);

        PaymentOptimizer.processSingleOrderPayment(order, reductions, methodBalances, fundsUsed);

        assertEquals(BigDecimal.valueOf(10), fundsUsed.get("PUNKTY"));
        assertEquals(BigDecimal.valueOf(80), fundsUsed.get("mZysk"));
        assertEquals(BigDecimal.valueOf(10), methodBalances.get("PUNKTY"));
        assertEquals(BigDecimal.valueOf(20), methodBalances.get("mZysk"));
    }

    @Test
    void testFallbackToMethodWhenPartialPointsNotAvailable() {
        Order order = new Order("ORDER2", BigDecimal.valueOf(50), List.of("BosBankrut"));
        Map<String, BigDecimal> reductions = Map.of("BosBankrut", BigDecimal.valueOf(5));

        Map<String, BigDecimal> methodBalances = new HashMap<>();
        methodBalances.put("PUNKTY", BigDecimal.valueOf(4));
        methodBalances.put("BosBankrut", BigDecimal.valueOf(100));

        Map<String, BigDecimal> fundsUsed = new HashMap<>();
        fundsUsed.put("PUNKTY", BigDecimal.ZERO);
        fundsUsed.put("BosBankrut", BigDecimal.ZERO);

        PaymentOptimizer.processSingleOrderPayment(order, reductions, methodBalances, fundsUsed);

        assertEquals(BigDecimal.ZERO, fundsUsed.get("PUNKTY"));
        assertEquals(BigDecimal.valueOf(45), fundsUsed.get("BosBankrut"));
        assertEquals(BigDecimal.valueOf(55), methodBalances.get("BosBankrut"));
    }

    @Test
    void testFallbackToAnyAvailableMethod() {
        Order order = new Order("ORDER3", BigDecimal.valueOf(30), List.of());
        Map<String, BigDecimal> reductions = new HashMap<>();

        Map<String, BigDecimal> methodBalances = new HashMap<>();
        methodBalances.put("mZysk", BigDecimal.valueOf(10));
        methodBalances.put("BosBankrut", BigDecimal.valueOf(25));

        Map<String, BigDecimal> fundsUsed = new HashMap<>();
        fundsUsed.put("mZysk", BigDecimal.ZERO);
        fundsUsed.put("BosBankrut", BigDecimal.ZERO);

        PaymentOptimizer.processSingleOrderPayment(order, reductions, methodBalances, fundsUsed);

        assertEquals(BigDecimal.valueOf(5), fundsUsed.get("mZysk"));
        assertEquals(BigDecimal.valueOf(25), fundsUsed.get("BosBankrut"));
    }

    @Test
    void testPartialPointsThenFallback() {
        Order order = new Order("ORDER5", BigDecimal.valueOf(50), List.of());

        Map<String, BigDecimal> reductions = Map.of("PartialPoints", BigDecimal.valueOf(10));

        Map<String, BigDecimal> methodBalances = new HashMap<>();
        methodBalances.put("PUNKTY", BigDecimal.valueOf(10));
        methodBalances.put("mZysk", BigDecimal.valueOf(30));
        methodBalances.put("BosBankrut", BigDecimal.valueOf(10));

        Map<String, BigDecimal> fundsUsed = new HashMap<>();
        methodBalances.keySet().forEach(k -> fundsUsed.put(k, BigDecimal.ZERO));

        PaymentOptimizer.processSingleOrderPayment(order, reductions, methodBalances, fundsUsed);
        assertEquals(BigDecimal.valueOf(10), fundsUsed.get("PUNKTY"));
        assertEquals(BigDecimal.valueOf(30), fundsUsed.get("mZysk"));
    }


}

