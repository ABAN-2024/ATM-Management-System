import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Complaint {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String ticketId;
    private final String category;
    private final String priority;
    private final String details;
    private final String status;
    private final LocalDateTime createdAt;

    public Complaint(String ticketId, String category, String priority, String details) {
        this(ticketId, category, priority, details, "Open", LocalDateTime.now());
    }

    public Complaint(String ticketId, String category, String priority, String details, String status, LocalDateTime createdAt) {
        this.ticketId = ticketId;
        this.category = category;
        this.priority = priority;
        this.details = details;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void printSummary() {
        System.out.println("Ticket: " + ticketId);
        System.out.println("Category: " + category);
        System.out.println("Priority: " + priority);
        System.out.println("Status: " + status);
        System.out.println("Created: " + createdAt.format(FORMATTER));
        System.out.println("Details: " + details);
    }
}
