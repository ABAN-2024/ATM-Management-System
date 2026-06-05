import java.util.Scanner;

public class ATM {
    private final Bank bank;
    private final Scanner scanner;
    private Customer currentCustomer;

    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printHeader();
        login();

        if (currentCustomer != null) {
            showMenu();
        }

        System.out.println("Thank you for using the ATM.");
    }

    private void login() {
        System.out.print("Enter card number: ");
        String cardNumber = scanner.nextLine().trim();

        Customer customer = bank.findCustomerByCardNumber(cardNumber);
        if (customer == null) {
            System.out.println("Card number not found.");
            return;
        }

        Card card = customer.getCard();
        while (!card.isLocked()) {
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            if (card.validatePin(pin)) {
                currentCustomer = customer;
                System.out.println("Welcome, " + customer.getName() + ".");
                return;
            }
        }
    }

    private void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("1. Check balance");
            System.out.println("2. Withdraw money");
            System.out.println("3. Deposit money");
            System.out.println("4. Transfer funds");
            System.out.println("5. Change PIN");
            System.out.println("6. Show account details");
            System.out.println("7. Raise complaint");
            System.out.println("8. View complaints");
            System.out.println("9. Mini statement");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showBalance();
                    break;
                case "2":
                    withdraw();
                    break;
                case "3":
                    deposit();
                    break;
                case "4":
                    transferFunds();
                    break;
                case "5":
                    changePin();
                    break;
                case "6":
                    showAccountDetails();
                    break;
                case "7":
                    raiseComplaint();
                    break;
                case "8":
                    viewComplaints();
                    break;
                case "9":
                    printMiniStatement();
                    break;
                case "10":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1 to 10.");
            }
        }
    }

    private void showBalance() {
        System.out.printf("Current balance: %.2f%n", currentCustomer.getAccount().getBalance());
    }

    private void withdraw() {
        double amount = readAmount("Enter withdrawal amount: ");
        if (currentCustomer.getAccount().withdraw(amount)) {
            currentCustomer.addTransaction("Withdrawal", -amount, "ATM cash withdrawal");
            showBalance();
        }
    }

    private void deposit() {
        double amount = readAmount("Enter deposit amount: ");
        if (currentCustomer.getAccount().deposit(amount)) {
            currentCustomer.addTransaction("Deposit", amount, "ATM cash deposit");
            showBalance();
        }
    }

    private void transferFunds() {
        System.out.print("Enter recipient account number: ");
        String recipientAccountNumber = scanner.nextLine().trim();
        Customer recipient = bank.findCustomerByAccountNumber(recipientAccountNumber);

        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        if (recipient == currentCustomer) {
            System.out.println("You cannot transfer money to the same account.");
            return;
        }

        double amount = readAmount("Enter transfer amount: ");
        if (currentCustomer.getAccount().withdraw(amount)) {
            recipient.getAccount().deposit(amount);
            currentCustomer.addTransaction(
                    "Transfer",
                    -amount,
                    "To " + recipient.getAccount().getAccountNumber());
            recipient.addTransaction(
                    "Transfer",
                    amount,
                    "From " + currentCustomer.getAccount().getAccountNumber());
            System.out.printf("Transferred %.2f to %s (%s).%n",
                    amount,
                    recipient.getName(),
                    recipient.getAccount().getAccountNumber());
            showBalance();
        }
    }

    private void printMiniStatement() {
        if (currentCustomer.getLastTransactions(5).isEmpty()) {
            System.out.println("No transactions available for mini statement.");
            return;
        }

        System.out.println();
        System.out.println("--- Mini statement: last 5 transactions ---");
        System.out.println("Date             | Type         |     Amount | Details");
        System.out.println("------------------------------------------------------");
        for (Transaction transaction : currentCustomer.getLastTransactions(5)) {
            transaction.printSummary();
        }
    }

    private void changePin() {
        System.out.print("Enter old PIN: ");
        String oldPin = scanner.nextLine().trim();

        System.out.print("Enter new 4-digit PIN: ");
        String newPin = scanner.nextLine().trim();

        currentCustomer.getCard().changePin(oldPin, newPin);
    }

    private void showAccountDetails() {
        Account account = currentCustomer.getAccount();
        System.out.println("Customer name: " + currentCustomer.getName());
        System.out.println("Account number: " + account.getAccountNumber());
        System.out.println("Card number: " + currentCustomer.getCard().getCardNumber());
        showBalance();
    }

    private void raiseComplaint() {
        System.out.println();
        System.out.println("Complaint categories");
        System.out.println("1. Cash withdrawal issue");
        System.out.println("2. Deposit issue");
        System.out.println("3. Card issue");
        System.out.println("4. PIN or login issue");
        System.out.println("5. Machine condition");
        System.out.println("6. Other");
        System.out.print("Choose category: ");
        String category = readComplaintCategory(scanner.nextLine().trim());

        System.out.println();
        System.out.println("Priority");
        System.out.println("1. Normal");
        System.out.println("2. High");
        System.out.println("3. Urgent");
        System.out.print("Choose priority: ");
        String priority = readComplaintPriority(scanner.nextLine().trim());

        String details = readComplaintDetails();
        Complaint complaint = currentCustomer.raiseComplaint(category, priority, details);

        System.out.println("Complaint submitted successfully.");
        System.out.println("Your ticket ID is " + complaint.getTicketId() + ".");
    }

    private void viewComplaints() {
        if (currentCustomer.getComplaints().isEmpty()) {
            System.out.println("No complaints raised yet.");
            return;
        }

        System.out.println();
        System.out.println("--- Your complaints ---");
        for (Complaint complaint : currentCustomer.getComplaints()) {
            complaint.printSummary();
            System.out.println("-----------------------");
        }
    }

    private double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String readComplaintCategory(String choice) {
        switch (choice) {
            case "1":
                return "Cash withdrawal issue";
            case "2":
                return "Deposit issue";
            case "3":
                return "Card issue";
            case "4":
                return "PIN or login issue";
            case "5":
                return "Machine condition";
            case "6":
                return "Other";
            default:
                System.out.println("Invalid category. Saving as Other.");
                return "Other";
        }
    }

    private String readComplaintPriority(String choice) {
        switch (choice) {
            case "1":
                return "Normal";
            case "2":
                return "High";
            case "3":
                return "Urgent";
            default:
                System.out.println("Invalid priority. Saving as Normal.");
                return "Normal";
        }
    }

    private String readComplaintDetails() {
        while (true) {
            System.out.print("Describe the issue: ");
            String details = scanner.nextLine().trim();

            if (details.length() >= 10) {
                return details;
            }

            System.out.println("Please enter at least 10 characters.");
        }
    }

    private void printHeader() {
        System.out.println("==============================");
        System.out.println("      ATM Management System");
        System.out.println("==============================");
    }

    public static void main(String[] args) {
        Bank bank = new Bank();

        bank.addCustomer(new Customer(
                "Ahmed Mohamed",
                new Card("1234567890", "1234"),
                new Account("ACC-001", 5000.00)));

        bank.addCustomer(new Customer(
                "Sara Ali",
                new Card("9876543210", "5678"),
                new Account("ACC-002", 12000.00)));

        ATM atm = new ATM(bank);
        atm.start();
    }
}
