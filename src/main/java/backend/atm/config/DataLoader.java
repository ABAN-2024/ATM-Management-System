package backend.atm.config;

import backend.atm.model.Account;
import backend.atm.model.Card;
import backend.atm.model.Customer;
import backend.atm.model.Transaction;
import backend.atm.repository.CustomerRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public ApplicationRunner initDatabase(CustomerRepository customerRepository) {
        return args -> {
            if (customerRepository.count() > 0) {
                return;
            }

            Customer firstCustomer = new Customer("Ahmed Mohamed", new Card("1234567890", "1234"),
                    new Account("ACC-001", 5000));
            firstCustomer.addTransaction(new Transaction("Deposit", 5000, "Opening balance"));

            Customer secondCustomer = new Customer("Sara Ali", new Card("9876543210", "4321"),
                    new Account("ACC-002", 2500));
            secondCustomer.addTransaction(new Transaction("Deposit", 2500, "Opening balance"));

            firstCustomer.addTransaction(new Transaction("Withdrawal", 250, "ATM withdrawal"));
            secondCustomer.addTransaction(new Transaction("Deposit", 500, "Salary credit"));

            customerRepository.saveAll(List.of(firstCustomer, secondCustomer));
        };
    }
}
