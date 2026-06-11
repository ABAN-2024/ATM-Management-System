package backend.atm.dto;

public class TransactionDto {
    private Long id;
    private String type;
    private double amount;
    private String details;
    private String createdAt;

    public TransactionDto() {
    }

    public TransactionDto(Long id, String type, double amount, String details, String createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
