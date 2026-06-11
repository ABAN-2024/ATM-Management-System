package backend.atm.dto;

public class TransactionRequest {
    private double amount;

    public TransactionRequest() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
