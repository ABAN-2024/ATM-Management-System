import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionHistory {
    private final List<Transaction> transactions;

    public TransactionHistory() {
        this.transactions = new ArrayList<>();
    }

    public void add(String type, double amount, String details) {
        transactions.add(0, new Transaction(type, amount, details));
    }

    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getLast(int limit) {
        int endIndex = Math.min(limit, transactions.size());
        return Collections.unmodifiableList(transactions.subList(0, endIndex));
    }

    public List<Transaction> getAll() {
        return Collections.unmodifiableList(transactions);
    }

    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    public int size() {
        return transactions.size();
    }
}
