package backend.atm.controller;

import backend.atm.dto.*;
import backend.atm.service.AtmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CustomerController {

    private final AtmService atmService;

    public CustomerController(AtmService atmService) {
        this.atmService = atmService;
    }

    @GetMapping("/customers")
    public List<CustomerDto> getAllCustomers() {
        return atmService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id) {
        return atmService.getCustomerById(id);
    }

    @PostMapping("/customers")
    public CustomerDto createCustomer(@RequestBody CustomerCreateRequest request) {
        return atmService.createCustomer(request);
    }

    @PostMapping("/auth/login")
    public CustomerDto login(@RequestBody LoginRequest request) {
        return atmService.login(request);
    }

    @PostMapping("/customers/{id}/deposit")
    public CustomerDto deposit(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return atmService.deposit(id, request);
    }

    @PostMapping("/customers/{id}/withdraw")
    public CustomerDto withdraw(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return atmService.withdraw(id, request);
    }

    @PostMapping("/customers/{id}/transfer")
    public CustomerDto transfer(@PathVariable Long id, @RequestBody TransferRequest request) {
        return atmService.transfer(id, request);
    }

    @PostMapping("/customers/{id}/change-pin")
    public CustomerDto changePin(@PathVariable Long id, @RequestBody ChangePinRequest request) {
        return atmService.changePin(id, request);
    }

    @PostMapping("/customers/{id}/complaints")
    public ComplaintDto raiseComplaint(@PathVariable Long id, @RequestBody ComplaintRequest request) {
        return atmService.raiseComplaint(id, request);
    }

    @GetMapping("/customers/{id}/complaints")
    public List<ComplaintDto> getComplaints(@PathVariable Long id) {
        return atmService.getComplaints(id);
    }

    @GetMapping("/customers/{id}/transactions")
    public List<TransactionDto> getTransactions(@PathVariable Long id) {
        return atmService.getTransactions(id);
    }
}
