import java.math.BigDecimal;

public class PaymentMethod {
    private String id;
    private BigDecimal discount;
    private BigDecimal limit;

    public String getId() { return id; }

    public BigDecimal getDiscount() { return discount; }

    public BigDecimal getLimit() { return limit; }
}
