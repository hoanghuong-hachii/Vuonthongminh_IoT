package com.example.smart_garden.jsonControl;

public class Params {
    private int pin;
    private boolean enabled;

    public Params(int pin, boolean enabled) {
        this.pin = pin;
        this.enabled = enabled;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
