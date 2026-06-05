import java.util.ArrayList;
import java.util.List;

public class Bank {
    private final List<Customer> customers;

    public Bank() {
        customers = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer findCustomerByCardNumber(String cardNumber) {
        for (Customer customer : customers) {
            if (customer.getCard().getCardNumber().equals(cardNumber)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomerByAccountNumber(String accountNumber) {
        for (Customer customer : customers) {
            if (customer.getAccount().getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return customer;
            }
        }
        return null;
    }
}
