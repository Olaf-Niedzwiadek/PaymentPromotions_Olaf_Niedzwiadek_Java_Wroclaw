import java.math.BigDecimal;

public class PaymentMethod {
    private String id;
    private BigDecimal discount;
    private BigDecimal limit;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getLimit() { return limit; }
    public void setLimit(BigDecimal limit) { this.limit = limit; }
}
