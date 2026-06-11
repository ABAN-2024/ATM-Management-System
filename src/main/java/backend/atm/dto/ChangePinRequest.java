package backend.atm.dto;

public class ChangePinRequest {
    private String currentPin;
    private String newPin;

    public ChangePinRequest() {
    }

    public String getCurrentPin() {
        return currentPin;
    }

    public void setCurrentPin(String currentPin) {
        this.currentPin = currentPin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
}
