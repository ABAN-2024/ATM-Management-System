package backend.atm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cardNumber", column = @Column(name = "card_number")),
            @AttributeOverride(name = "pin", column = @Column(name = "card_pin")),
            @AttributeOverride(name = "locked", column = @Column(name = "card_locked")),
            @AttributeOverride(name = "failedAttempts", column = @Column(name = "card_failed_attempts"))
    })
    private Card card;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "accountNumber", column = @Column(name = "account_number")),
            @AttributeOverride(name = "balance", column = @Column(name = "account_balance"))
    })
    private Account account;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    @JsonManagedReference
    private List<Complaint> complaints = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    @JsonManagedReference
    private List<Transaction> transactions = new ArrayList<>();

    public Customer() {
    }

    public Customer(String name, Card card, Account account) {
        this.name = name;
        this.card = card;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Complaint> getComplaints() {
        return Collections.unmodifiableList(complaints);
    }

    public Complaint raiseComplaint(String category, String priority, String details) {
        Complaint complaint = new Complaint(createComplaintTicketId(), category, priority, details);
        addComplaint(complaint);
        return complaint;
    }

    public void addComplaint(Complaint complaint) {
        complaint.setCustomer(this);
        complaints.add(0, complaint);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void addTransaction(Transaction transaction) {
        transaction.setCustomer(this);
        transactions.add(0, transaction);
    }

    public void addTransaction(String type, double amount, String details) {
        addTransaction(new Transaction(type, amount, details));
    }

    public List<Transaction> getLastTransactions(int limit) {
        int endIndex = Math.min(limit, transactions.size());
        return Collections.unmodifiableList(transactions.subList(0, endIndex));
    }

    public String createComplaintTicketId() {
        String accountDigits = account.getAccountNumber().replaceAll("\\D", "");
        String suffix = accountDigits.length() > 3
                ? accountDigits.substring(accountDigits.length() - 3)
                : accountDigits;
        return String.format("CMP-%s-%03d", suffix, complaints.size() + 1);
    }
}
