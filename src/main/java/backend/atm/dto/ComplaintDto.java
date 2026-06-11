package backend.atm.dto;

public class ComplaintDto {
    private Long id;
    private String ticketId;
    private String category;
    private String priority;
    private String status;
    private String details;
    private String createdAt;

    public ComplaintDto() {
    }

    public ComplaintDto(Long id, String ticketId, String category, String priority, String status, String details,
            String createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
