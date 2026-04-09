package com.app.blooddonor.exception;

public class DonorProfileNotFoundException extends RuntimeException {
    public DonorProfileNotFoundException(String message) {
        super(message);
    }
}
