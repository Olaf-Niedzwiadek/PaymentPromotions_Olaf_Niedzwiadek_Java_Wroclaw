import java.math.BigDecimal;
import java.util.List;

public class Order {
    private String id;
    private BigDecimal value;
    private List<String> promotions;

    public String getId() { return id; }

    public BigDecimal getValue() { return value; }

    public List<String> getPromotions() { return promotions; }
}
