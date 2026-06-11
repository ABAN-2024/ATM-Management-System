package backend.atm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Card {
    private static final int MAX_ATTEMPTS = 3;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_pin")
    private String pin;

    @Column(name = "card_locked")
    private boolean locked;

    @Column(name = "card_failed_attempts")
    private int failedAttempts;

    public Card() {
    }

    public Card(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.locked = false;
        this.failedAttempts = 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public void restoreState(boolean locked, int failedAttempts) {
        this.locked = locked;
        this.failedAttempts = failedAttempts;
    }

    public boolean validatePin(String inputPin) {
        if (locked) {
            return false;
        }

        if (pin.equals(inputPin)) {
            failedAttempts = 0;
            return true;
        }

        failedAttempts++;

        if (failedAttempts >= MAX_ATTEMPTS) {
            lockCard();
        }

        return false;
    }

    public boolean changePin(String oldPin, String newPin) {
        if (locked) {
            return false;
        }

        if (!pin.equals(oldPin)) {
            return false;
        }

        if (!isValidPin(newPin)) {
            return false;
        }

        pin = newPin;
        failedAttempts = 0;
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
