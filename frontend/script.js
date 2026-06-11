let accounts = [];

const apiBaseUrl = "http://localhost:8080/api";
const customerApiUrl = `${apiBaseUrl}/customers`;
let useBackend = true;

function applyBackendCustomers(customers) {
    if (!Array.isArray(customers) || customers.length === 0) {
        return false;
    }

    const backendAccounts = customers
        .filter((customer) => customer.accountNumber && customer.name && typeof customer.balance === "number")
        .map((customer, index) => ({
            id: customer.id,
            name: customer.name,
            cardNumber: customer.cardNumber || customer.accountNumber || `0000${String(index + 1).padStart(4, "0")}`,
            pin: customer.pin || null,
            accountNumber: customer.accountNumber,
            balance: customer.balance,
            locked: customer.locked || false,
            failedAttempts: customer.failedAttempts || 0,
            transactions: customer.transactions || [],
            complaints: customer.complaints || [],
            history: customer.history || []
        }));

    if (backendAccounts.length === 0) {
        return false;
    }

    useBackend = true;
    accounts = backendAccounts;
    return true;
}

async function loadCustomers() {
    try {
        const response = await fetch(customerApiUrl);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const customers = await response.json();
        const backendUsed = applyBackendCustomers(customers);
        console.log("Loaded customers from backend:", customers, backendUsed ? "Using backend customer data" : "Using sample accounts");
        return customers;
    } catch (error) {
        console.error("Failed to load customers:", error);
        setMessage("Backend unavailable. Please start the backend and refresh.", "error");
        return [];
    }
}

function mapBackendCustomer(customer) {
    return {
        id: customer.id,
        name: customer.name,
        cardNumber: customer.cardNumber,
        pin: customer.pin || null,
        accountNumber: customer.accountNumber,
        balance: customer.balance,
        locked: customer.locked || false,
        failedAttempts: customer.failedAttempts || 0,
        transactions: customer.transactions || [],
        complaints: customer.complaints || [],
        history: []
    };
}

function updateLocalAccount(customer) {
    if (!customer || !customer.id) {
        return;
    }

    const index = accounts.findIndex((account) => account.id === customer.id);
    if (index !== -1) {
        accounts[index] = mapBackendCustomer(customer);
    }
}

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);
    if (!response.ok) {
        const errorBody = await response.json().catch(() => null);
        const message = errorBody?.message || response.statusText || `HTTP ${response.status}`;
        throw new Error(message);
    }
    return response.json();
}

async function backendLogin(cardNumber, pin) {
    return fetchJson(`${apiBaseUrl}/auth/login`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({cardNumber, pin})
    });
}

async function backendAction(path, body) {
    return fetchJson(`${apiBaseUrl}${path}`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(body)
    });
}

const maxAttempts = 3;
let currentAccount = null;
let currentOperation = "withdraw";
let balanceVisible = false;

const loginView = document.querySelector("#loginView");
const dashboardView = document.querySelector("#dashboardView");
const loginForm = document.querySelector("#loginForm");
const transactionForm = document.querySelector("#transactionForm");
const messageBox = document.querySelector("#messageBox");
const customerName = document.querySelector("#customerName");
const accountNumber = document.querySelector("#accountNumber");
const balanceDisplay = document.querySelector("#balanceDisplay");
const balanceToggle = document.querySelector("#balanceToggle");
const historyList = document.querySelector("#historyList");
const amountField = document.querySelector("#amountField");
const amountInput = document.querySelector("#amountInput");
const transferFields = document.querySelector("#transferFields");
const recipientAccountInput = document.querySelector("#recipientAccountInput");
const pinFields = document.querySelector("#pinFields");
const oldPinInput = document.querySelector("#oldPinInput");
const newPinInput = document.querySelector("#newPinInput");
const complaintFields = document.querySelector("#complaintFields");
const complaintCategory = document.querySelector("#complaintCategory");
const complaintPriority = document.querySelector("#complaintPriority");
const complaintDetails = document.querySelector("#complaintDetails");
const complaintList = document.querySelector("#complaintList");
const complaintCount = document.querySelector("#complaintCount");
const submitOperation = document.querySelector("#submitOperation");

loginForm.addEventListener("submit", handleLogin);
transactionForm.addEventListener("submit", handleTransaction);
balanceToggle.addEventListener("click", toggleBalanceVisibility);
document.querySelector("#signOutButton").addEventListener("click", signOut);
document.querySelector("#clearHistoryButton").addEventListener("click", clearHistory);

document.querySelectorAll(".tab-button").forEach((button) => {
    button.addEventListener("click", () => setOperation(button.dataset.operation));
});

async function handleLogin(event) {
    event.preventDefault();

    const cardNumber = document.querySelector("#cardNumber").value.trim();
    const pin = document.querySelector("#pin").value.trim();

    if (useBackend) {
        try {
            const backendCustomer = await backendLogin(cardNumber, pin);
            currentAccount = mapBackendCustomer(backendCustomer);
            updateLocalAccount(backendCustomer);
            showDashboard(currentAccount);
            return;
        } catch (error) {
            setMessage(error.message, "error");
            return;
        }
    }

    const account = accounts.find(
        (item) => item.cardNumber === cardNumber || item.accountNumber === cardNumber
    );

    if (!account) {
        setMessage("Account not found.", "error");
        return;
    }

    if (account.locked) {
        setMessage("This card is locked. Please contact the bank.", "error");
        return;
    }

    if (account.pin !== pin) {
        account.failedAttempts += 1;

        if (account.failedAttempts >= maxAttempts) {
            account.locked = true;
            setMessage("Card locked after too many failed attempts.", "error");
            return;
        }

        setMessage(`Incorrect PIN. Attempt ${account.failedAttempts}/${maxAttempts}.`, "error");
        return;
    }

    account.failedAttempts = 0;
    currentAccount = account;
    showDashboard(currentAccount);
}

function showDashboard(account) {
    if (loginForm) loginForm.classList.add("hidden");
    if (dashboardView) dashboardView.classList.remove("hidden");
    setOperation("deposit");
    updateLocalAccount(account);
    updateDashboard();
    setMessage(`Welcome, ${account.name}!`, "success");
}

async function handleTransaction(event) {
    event.preventDefault();

    if (!currentAccount) {
        setMessage("Please sign in first.", "error");
        return;
    }

    if (currentOperation === "pin") {
        await changePin();
        return;
    }

    if (currentOperation === "complaint") {
        await raiseComplaint();
        return;
    }

    const amount = Number.parseFloat(amountInput.value);
    if (!Number.isFinite(amount) || amount <= 0) {
        setMessage("Enter an amount greater than zero.", "error");
        return;
    }

    if (currentOperation === "withdraw") {
        await withdraw(amount);
    } else if (currentOperation === "transfer") {
        await transfer(amount);
    } else {
        await deposit(amount);
    }

    amountInput.value = "";
    updateDashboard();
}

async function withdraw(amount) {
    if (useBackend && currentAccount.id) {
        try {
            const updated = await backendAction(`/customers/${currentAccount.id}/withdraw`, {amount});
            currentAccount = mapBackendCustomer(updated);
            updateLocalAccount(updated);
            setMessage(`Withdrawn ${formatMoney(amount)}.`, "success");
            return;
        } catch (error) {
            setMessage(error.message, "error");
            return;
        }
    }

    if (amount > currentAccount.balance) {
        setMessage("Insufficient balance.", "error");
        return;
    }

    currentAccount.balance -= amount;
    addHistory("Withdrawal", -amount, "financial");
    setMessage(`Withdrawn ${formatMoney(amount)}.`, "success");
}

async function deposit(amount) {
    if (useBackend && currentAccount.id) {
        try {
            const updated = await backendAction(`/customers/${currentAccount.id}/deposit`, {amount});
            currentAccount = mapBackendCustomer(updated);
            updateLocalAccount(updated);
            setMessage(`Deposited ${formatMoney(amount)}.`, "success");
            return;
        } catch (error) {
            setMessage(error.message, "error");
            return;
        }
    }

    currentAccount.balance += amount;
    addHistory("Deposit", amount, "financial");
    setMessage(`Deposited ${formatMoney(amount)}.`, "success");
}

async function transfer(amount) {
    const recipientAccountNumber = recipientAccountInput.value.trim().toUpperCase();

    if (useBackend && currentAccount.id) {
        try {
            const updated = await backendAction(`/customers/${currentAccount.id}/transfer`, {
                recipientAccountNumber,
                amount
            });
            currentAccount = mapBackendCustomer(updated);
            updateLocalAccount(updated);

            const recipient = accounts.find((account) => account.accountNumber.toUpperCase() === recipientAccountNumber);
            if (recipient && recipient.id && recipient.id !== currentAccount.id) {
                recipient.balance += amount;
            }

            setMessage(`Transferred ${formatMoney(amount)} to ${recipientAccountNumber}.`, "success");
            return;
        } catch (error) {
            setMessage(error.message, "error");
            return;
        }
    }

    const recipient = accounts.find((account) => account.accountNumber.toUpperCase() === recipientAccountNumber);

    if (!recipient) {
        setMessage("Recipient account not found.", "error");
        return;
    }

    if (recipient === currentAccount) {
        setMessage("You cannot transfer money to the same account.", "error");
        return;
    }

    if (amount > currentAccount.balance) {
        setMessage("Insufficient balance.", "error");
        return;
    }

    currentAccount.balance -= amount;
    recipient.balance += amount;
    addHistory(`Transfer to ${recipient.accountNumber}`, -amount, "financial");
    recipient.history.unshift(createHistoryEntry(`Transfer from ${currentAccount.accountNumber}`, amount, "financial"));
    setMessage(`Transferred ${formatMoney(amount)} to ${recipient.name}.`, "success");
}

async function changePin() {
    if (useBackend && currentAccount.id) {
        const currentPin = oldPinInput.value.trim();
        const newPin = newPinInput.value.trim();

        try {
            const updated = await backendAction(`/customers/${currentAccount.id}/change-pin`, {
                currentPin,
                newPin
            });
            currentAccount = mapBackendCustomer(updated);
            updateLocalAccount(updated);
            oldPinInput.value = "";
            newPinInput.value = "";
            setMessage("PIN changed successfully.", "success");
            return;
        } catch (error) {
            setMessage(error.message, "error");
            return;
        }
    }

    const oldPin = oldPinInput.value.trim();
    const newPin = newPinInput.value.trim();

    if (oldPin !== currentAccount.pin) {
        setMessage("Old PIN is incorrect.", "error");
        return;
    }

    if (!/^\d{4}$/.test(newPin)) {
        setMessage("New PIN must be exactly 4 digits.", "error");
        return;
    }

    currentAccount.pin = newPin;
    oldPinInput.value = "";
    newPinInput.value = "";
    addHistory("PIN changed", 0, "service");
    updateDashboard();
    setMessage("PIN changed successfully.", "success");
}

async function raiseComplaint() {
    const details = complaintDetails.value.trim();

    if (details.length < 10) {
        setMessage("Please enter at least 10 characters for the complaint details.", "error");
        return;
    }

    if (useBackend && currentAccount.id) {
        try {
            const complaint = await backendAction(`/customers/${currentAccount.id}/complaints`, {
                category: complaintCategory.value,
                priority: complaintPriority.value,
                details
            });
            currentAccount = mapBackendCustomer(await fetchJson(`${apiBaseUrl}/customers/${currentAccount.id}`));
            updateLocalAccount(currentAccount);
            transactionForm.reset();
            setMessage(`Complaint submitted. Ticket ${complaint.ticketId}.`, "success");
            return;
        } catch (error) {
            setMessage(error.message, "error");
            return;
        }
    }

    const complaint = {
        ticketId: createTicketId(),
        category: complaintCategory.value,
        priority: complaintPriority.value,
        details,
        status: "Open",
        createdAt: new Date().toLocaleString([], {
            dateStyle: "medium",
            timeStyle: "short"
        })
    };

    currentAccount.complaints.unshift(complaint);
    addHistory("Complaint raised", 0, "service");
    transactionForm.reset();
    updateDashboard();
    setMessage(`Complaint submitted. Ticket ${complaint.ticketId}.`, "success");
}

function setOperation(operation) {
    currentOperation = operation;

    document.querySelectorAll(".tab-button").forEach((button) => {
        button.classList.toggle("active", button.dataset.operation === operation);
    });

    const isPinOperation = operation === "pin";
    const isComplaintOperation = operation === "complaint";
    const isTransferOperation = operation === "transfer";
    amountField.classList.toggle("hidden", isPinOperation || isComplaintOperation);
    transferFields.classList.toggle("hidden", !isTransferOperation);
    pinFields.classList.toggle("hidden", !isPinOperation);
    complaintFields.classList.toggle("hidden", !isComplaintOperation);
    submitOperation.textContent = getOperationButtonText(operation);

    transactionForm.reset();
}

function addHistory(label, amount, type) {
    if (useBackend && Array.isArray(currentAccount.transactions)) {
        return;
    }

    if (!currentAccount.history) {
        currentAccount.history = [];
    }

    currentAccount.history.unshift(createHistoryEntry(label, amount, type));
}

function createHistoryEntry(label, amount, type) {
    const time = new Date().toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit"
    });

    return {
        label,
        amount,
        type,
        time
    };
}

function clearHistory() {
    if (!currentAccount) {
        return;
    }

    if (useBackend) {
        setMessage("Cannot clear backend transaction history.", "error");
        return;
    }

    currentAccount.history = [];
    updateHistory();
    setMessage("Activity cleared.", "success");
}

function updateDashboard() {
    customerName.textContent = currentAccount.name;
    accountNumber.textContent = currentAccount.accountNumber;
    updateBalanceDisplay();
    updateHistory();
    updateComplaints();
}

function updateBalanceDisplay() {
    balanceDisplay.textContent = balanceVisible ? formatMoney(currentAccount.balance) : "••••••";
    balanceToggle.setAttribute("aria-label", balanceVisible ? "Hide balance" : "Show balance");
    balanceToggle.setAttribute("aria-pressed", String(balanceVisible));
}

function toggleBalanceVisibility() {
    if (!currentAccount) {
        return;
    }

    balanceVisible = !balanceVisible;
    updateBalanceDisplay();
    setMessage(balanceVisible ? "Balance is visible." : "Balance is hidden.", "success");
}

function updateHistory() {
    historyList.innerHTML = "";

    const statementEntries = getStatementEntries();

    if (statementEntries.length === 0) {
        const emptyItem = document.createElement("li");
        emptyItem.innerHTML = "<strong>No transactions yet</strong><span>-</span>";
        historyList.appendChild(emptyItem);
        return;
    }

    statementEntries.forEach((entry) => {
        const item = document.createElement("li");
        const amount = entry.amount === 0 ? "" : formatSignedMoney(entry.amount);
        item.innerHTML = `<strong>${entry.label}<span>${entry.time}</span></strong><span>${amount}</span>`;
        historyList.appendChild(item);
    });
}

function getStatementEntries() {
    if (Array.isArray(currentAccount.transactions) && currentAccount.transactions.length > 0) {
        return currentAccount.transactions.slice(0, 5).map((transaction) => ({
            label: transaction.type,
            amount: transaction.amount,
            type: "financial",
            time: transaction.createdAt
        }));
    }

    return (currentAccount.history || [])
        .filter((entry) => entry.type === "financial")
        .slice(0, 5);
}

function updateComplaints() {
    complaintList.innerHTML = "";
    const openCount = currentAccount.complaints.filter((complaint) => complaint.status === "Open").length;
    complaintCount.textContent = `${openCount} open`;

    if (currentAccount.complaints.length === 0) {
        const emptyItem = document.createElement("li");
        emptyItem.innerHTML = "<strong>No complaints raised</strong><span>-</span>";
        complaintList.appendChild(emptyItem);
        return;
    }

    currentAccount.complaints.forEach((complaint) => {
        const item = document.createElement("li");
        const title = document.createElement("strong");
        const ticket = document.createElement("span");
        const meta = document.createElement("span");
        const details = document.createElement("p");

        title.textContent = complaint.category;
        ticket.className = "ticket-badge";
        ticket.textContent = complaint.ticketId;
        title.appendChild(ticket);

        meta.textContent = `${complaint.priority} priority | ${complaint.status} | ${complaint.createdAt}`;
        details.textContent = complaint.details;

        item.appendChild(title);
        item.appendChild(meta);
        item.appendChild(details);
        complaintList.appendChild(item);
    });
}

function signOut() {
    currentAccount = null;
    balanceVisible = false;
    dashboardView.classList.add("hidden");
    loginView.classList.remove("hidden");
    setMessage("Signed out.", "success");
}

function setMessage(text, type) {
    messageBox.textContent = text;
    messageBox.className = `message ${type || ""}`.trim();
}

function formatMoney(value) {
    return value.toLocaleString("en-US", {
        style: "currency",
        currency: "USD"
    });
}

function formatSignedMoney(value) {
    const sign = value > 0 ? "+" : "-";
    return `${sign}${formatMoney(Math.abs(value))}`;
}

function getOperationButtonText(operation) {
    if (operation === "withdraw") {
        return "Withdraw";
    }

    if (operation === "deposit") {
        return "Deposit";
    }

    if (operation === "transfer") {
        return "Transfer";
    }

    if (operation === "pin") {
        return "Change PIN";
    }

    return "Submit complaint";
}

function createTicketId() {
    const accountSuffix = currentAccount.accountNumber.replace(/\D/g, "").slice(-3);
    const sequence = String(currentAccount.complaints.length + 1).padStart(3, "0");
    return `CMP-${accountSuffix}-${sequence}`;
}

loadCustomers();
