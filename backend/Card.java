public class Card {
    private static final int MAX_ATTEMPTS = 3;

    private final String cardNumber;
    private String pin;
    private boolean locked;
    private int failedAttempts;

    public Card(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.locked = false;
        this.failedAttempts = 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void restoreState(boolean locked, int failedAttempts) {
        this.locked = locked;
        this.failedAttempts = failedAttempts;
    }

    public boolean validatePin(String inputPin) {
        if (locked) {
            System.out.println("This card is locked. Please contact the bank.");
            return false;
        }

        if (pin.equals(inputPin)) {
            failedAttempts = 0;
            return true;
        }

        failedAttempts++;
        System.out.printf("Incorrect PIN. Attempt %d/%d.%n", failedAttempts, MAX_ATTEMPTS);

        if (failedAttempts >= MAX_ATTEMPTS) {
            lockCard();
        }

        return false;
    }

    public boolean changePin(String oldPin, String newPin) {
        if (locked) {
            System.out.println("Cannot change PIN because this card is locked.");
            return false;
        }

        if (!pin.equals(oldPin)) {
            System.out.println("Old PIN is incorrect.");
            return false;
        }

        if (!isValidPin(newPin)) {
            System.out.println("New PIN must be exactly 4 digits.");
            return false;
        }

        pin = newPin;
        failedAttempts = 0;
        System.out.println("PIN changed successfully.");
        return true;
    }

    private boolean isValidPin(String value) {
        return value != null && value.matches("\\d{4}");
    }

    private void lockCard() {
        locked = true;
        System.out.println("Card locked after too many failed attempts.");
    }
}
