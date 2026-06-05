const accounts = [
    {
        name: "Ahmed Mohamed",
        cardNumber: "1234567890",
        pin: "1234",
        accountNumber: "ACC-001",
        balance: 5000,
        locked: false,
        failedAttempts: 0,
        history: [],
        complaints: []
    },
    {
        name: "Sara Ali",
        cardNumber: "9876543210",
        pin: "5678",
        accountNumber: "ACC-002",
        balance: 12000,
        locked: false,
        failedAttempts: 0,
        history: [],
        complaints: []
    }
];

const maxAttempts = 3;
let currentAccount = null;
let currentOperation = "withdraw";
let balanceVisible = false;

const loginView = document.querySelector("#loginView");
const dashboardView = document.querySelector("#dashboardView");
const loginForm = document.querySelector("#loginForm");
const transactionForm = document.querySelector("#transactionForm");
const sessionStatus = document.querySelector("#sessionStatus");
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

document.querySelectorAll(".sample-account").forEach((button) => {
    button.addEventListener("click", () => {
        document.querySelector("#cardNumber").value = button.dataset.card;
        document.querySelector("#pin").value = button.dataset.pin;
    });
});

document.querySelectorAll(".tab-button").forEach((button) => {
    button.addEventListener("click", () => setOperation(button.dataset.operation));
});

function handleLogin(event) {
    event.preventDefault();

    const cardNumber = document.querySelector("#cardNumber").value.trim();
    const pin = document.querySelector("#pin").value.trim();
    const account = accounts.find((item) => item.cardNumber === cardNumber);

    if (!account) {
        setMessage("Card number not found.", "error");
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
    loginForm.reset();
    loginView.classList.add("hidden");
    dashboardView.classList.remove("hidden");
    sessionStatus.textContent = "Signed in";
    sessionStatus.classList.add("active");
    balanceVisible = false;
    setOperation("withdraw");
    updateDashboard();
    setMessage(`Welcome, ${account.name}.`, "success");
}

function handleTransaction(event) {
    event.preventDefault();

    if (!currentAccount) {
        setMessage("Please sign in first.", "error");
        return;
    }

    if (currentOperation === "pin") {
        changePin();
        return;
    }

    if (currentOperation === "complaint") {
        raiseComplaint();
        return;
    }

    const amount = Number.parseFloat(amountInput.value);
    if (!Number.isFinite(amount) || amount <= 0) {
        setMessage("Enter an amount greater than zero.", "error");
        return;
    }

    if (currentOperation === "withdraw") {
        withdraw(amount);
    } else if (currentOperation === "transfer") {
        transfer(amount);
    } else {
        deposit(amount);
    }

    amountInput.value = "";
    updateDashboard();
}

function withdraw(amount) {
    if (amount > currentAccount.balance) {
        setMessage("Insufficient balance.", "error");
        return;
    }

    currentAccount.balance -= amount;
    addHistory("Withdrawal", -amount, "financial");
    setMessage(`Withdrawn ${formatMoney(amount)}.`, "success");
}

function deposit(amount) {
    currentAccount.balance += amount;
    addHistory("Deposit", amount, "financial");
    setMessage(`Deposited ${formatMoney(amount)}.`, "success");
}

function transfer(amount) {
    const recipientAccountNumber = recipientAccountInput.value.trim().toUpperCase();
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

function changePin() {
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

function raiseComplaint() {
    const details = complaintDetails.value.trim();

    if (details.length < 10) {
        setMessage("Please enter at least 10 characters for the complaint details.", "error");
        return;
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

    const statementEntries = currentAccount.history
        .filter((entry) => entry.type === "financial")
        .slice(0, 5);

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
    sessionStatus.textContent = "Signed out";
    sessionStatus.classList.remove("active");
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
