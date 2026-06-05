import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String type;
    private final double amount;
    private final String details;
    private final LocalDateTime createdAt;

    public Transaction(String type, double amount, String details) {
        this(type, amount, details, LocalDateTime.now());
    }

    public Transaction(String type, double amount, String details, LocalDateTime createdAt) {
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void printSummary() {
        System.out.printf("%s | %-12s | %10.2f | %s%n",
                createdAt.format(FORMATTER),
                type,
                amount,
                details);
    }
}
