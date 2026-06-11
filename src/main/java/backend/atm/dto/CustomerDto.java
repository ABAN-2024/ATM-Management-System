package backend.atm.dto;

import java.util.List;

public class CustomerDto {
    private Long id;
    private String name;
    private String cardNumber;
    private boolean locked;
    private int failedAttempts;
    private String accountNumber;
    private double balance;
    private List<TransactionDto> transactions;
    private List<ComplaintDto> complaints;

    public CustomerDto() {
    }

    public CustomerDto(Long id, String name, String cardNumber, boolean locked, int failedAttempts,
                       String accountNumber, double balance,
                       List<TransactionDto> transactions, List<ComplaintDto> complaints) {
        this.id = id;
        this.name = name;
        this.cardNumber = cardNumber;
        this.locked = locked;
        this.failedAttempts = failedAttempts;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.transactions = transactions;
        this.complaints = complaints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public List<ComplaintDto> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<ComplaintDto> complaints) {
        this.complaints = complaints;
    }
}
