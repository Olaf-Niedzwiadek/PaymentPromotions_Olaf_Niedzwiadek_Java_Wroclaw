import java.math.BigDecimal;
import java.util.List;

public class PaymentMethod {
    private String id;
    private BigDecimal discount;
    private BigDecimal limit;

    public PaymentMethod(){}
    public PaymentMethod(String id, BigDecimal discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public String getId() { return id; }

    public BigDecimal getDiscount() { return discount; }

    public BigDecimal getLimit() { return limit; }
}
