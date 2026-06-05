import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {
    private final String name;
    private final Card card;
    private final Account account;
    private final List<Complaint> complaints;
    private final TransactionHistory transactionHistory;

    public Customer(String name, Card card, Account account) {
        this.name = name;
        this.card = card;
        this.account = account;
        this.complaints = new ArrayList<>();
        this.transactionHistory = new TransactionHistory();
    }

    public String getName() {
        return name;
    }

    public Card getCard() {
        return card;
    }

    public Account getAccount() {
        return account;
    }

    public Complaint raiseComplaint(String category, String priority, String details) {
        Complaint complaint = new Complaint(createComplaintTicketId(), category, priority, details);
        complaints.add(0, complaint);
        return complaint;
    }

    public void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public List<Complaint> getComplaints() {
        return Collections.unmodifiableList(complaints);
    }

    public void addTransaction(String type, double amount, String details) {
        transactionHistory.add(type, amount, details);
    }

    public List<Transaction> getLastTransactions(int limit) {
        return transactionHistory.getLast(limit);
    }

    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    private String createComplaintTicketId() {
        String accountDigits = account.getAccountNumber().replaceAll("\\D", "");
        String suffix = accountDigits.length() > 3
                ? accountDigits.substring(accountDigits.length() - 3)
                : accountDigits;
        return String.format("CMP-%s-%03d", suffix, complaints.size() + 1);

    }
}
