package backend.atm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "complaints")
public class Complaint {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketId;
    private String category;
    private String priority;
    private String details;
    private String status;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;

    public Complaint() {
    }

    public Complaint(String ticketId, String category, String priority, String details) {
        this(ticketId, category, priority, details, "Open", LocalDateTime.now());
    }

    public Complaint(String ticketId, String category, String priority, String details, String status,
            LocalDateTime createdAt) {
        this.ticketId = ticketId;
        this.category = category;
        this.priority = priority;
        this.details = details;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
