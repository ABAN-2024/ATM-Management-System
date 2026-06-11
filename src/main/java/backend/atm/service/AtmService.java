package backend.atm.service;

import backend.atm.dto.*;
import backend.atm.model.Card;
import backend.atm.model.Complaint;
import backend.atm.model.Customer;
import backend.atm.model.Transaction;
import backend.atm.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AtmService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final CustomerRepository repository;

    public AtmService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<CustomerDto> getAllCustomers() {
        return repository.findAll().stream().map(this::toCustomerDto).collect(Collectors.toList());
    }

    public CustomerDto getCustomerById(Long id) {
        return toCustomerDto(findCustomer(id));
    }

    public CustomerDto createCustomer(CustomerCreateRequest request) {
        Customer customer = new Customer(request.getName(), new Card(request.getCardNumber(), request.getPin()),
                new backend.atm.model.Account(request.getAccountNumber(), request.getOpeningBalance()));
        return toCustomerDto(repository.save(customer));
    }

    public CustomerDto login(LoginRequest request) {
        Customer customer = repository.findByCardCardNumber(request.getCardNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));

        Card card = customer.getCard();
        if (card.isLocked()) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "Card is locked");
        }

        if (!card.validatePin(request.getPin())) {
            repository.save(customer);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid PIN");
        }

        repository.save(customer);
        return toCustomerDto(customer);
    }

    public CustomerDto deposit(Long customerId, TransactionRequest request) {
        if (request.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deposit amount must be positive");
        }

        Customer customer = findCustomer(customerId);
        if (!customer.getAccount().deposit(request.getAmount())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deposit amount must be positive");
        }

        customer.addTransaction(new Transaction("Deposit", request.getAmount(), "Deposit"));
        return toCustomerDto(repository.save(customer));
    }

    public CustomerDto withdraw(Long customerId, TransactionRequest request) {
        if (request.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Withdrawal amount must be positive");
        }

        Customer customer = findCustomer(customerId);
        if (!customer.getAccount().withdraw(request.getAmount())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds or invalid amount");
        }

        customer.addTransaction(new Transaction("Withdrawal", -request.getAmount(), "Withdrawal"));
        return toCustomerDto(repository.save(customer));
    }

    public CustomerDto transfer(Long customerId, TransferRequest request) {
        if (request.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount must be positive");
        }

        Customer sender = findCustomer(customerId);
        Customer recipient = repository.findByAccountAccountNumber(request.getRecipientAccountNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient account not found"));

        if (recipient.getId().equals(sender.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot transfer to the same account");
        }

        if (!sender.getAccount().withdraw(request.getAmount())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds or invalid amount");
        }

        recipient.getAccount().deposit(request.getAmount());
        sender.addTransaction(new Transaction("Transfer Out", -request.getAmount(),
                "To " + recipient.getAccount().getAccountNumber()));
        recipient.addTransaction(
                new Transaction("Transfer In", request.getAmount(), "From " + sender.getAccount().getAccountNumber()));

        repository.save(recipient);
        return toCustomerDto(repository.save(sender));
    }

    public CustomerDto changePin(Long customerId, ChangePinRequest request) {
        Customer customer = findCustomer(customerId);
        if (!customer.getCard().changePin(request.getCurrentPin(), request.getNewPin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current PIN is incorrect or new PIN is invalid");
        }

        return toCustomerDto(repository.save(customer));
    }

    public ComplaintDto raiseComplaint(Long customerId, ComplaintRequest request) {
        Customer customer = findCustomer(customerId);
        Complaint complaint = customer.raiseComplaint(request.getCategory(), request.getPriority(),
                request.getDetails());
        repository.save(customer);
        return toComplaintDto(complaint);
    }

    public List<ComplaintDto> getComplaints(Long customerId) {
        return findCustomer(customerId).getComplaints().stream().map(this::toComplaintDto).collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactions(Long customerId) {
        return findCustomer(customerId).getTransactions().stream().map(this::toTransactionDto)
                .collect(Collectors.toList());
    }

    private Customer findCustomer(Long customerId) {
        return repository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    private CustomerDto toCustomerDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getCard().getCardNumber(),
                customer.getCard().isLocked(),
                customer.getCard().getFailedAttempts(),
                customer.getAccount().getAccountNumber(),
                customer.getAccount().getBalance(),
                customer.getTransactions().stream().map(this::toTransactionDto).collect(Collectors.toList()),
                customer.getComplaints().stream().map(this::toComplaintDto).collect(Collectors.toList()));
    }

    private TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDetails(),
                transaction.getCreatedAt().format(DATE_FORMATTER));
    }

    private ComplaintDto toComplaintDto(Complaint complaint) {
        return new ComplaintDto(
                complaint.getId(),
                complaint.getTicketId(),
                complaint.getCategory(),
                complaint.getPriority(),
                complaint.getStatus(),
                complaint.getDetails(),
                complaint.getCreatedAt().format(DATE_FORMATTER));
    }
}
