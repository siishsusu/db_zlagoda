package org.example.db_zlagoda.utils.Exceptions;

public class NegativeAmountException extends Exception {

    public NegativeAmountException() {
        super("Item amount cannot be negative.");
    }

    public NegativeAmountException(String message) {
        super(message);
    }

    public NegativeAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public NegativeAmountException(Throwable cause) {
        super(cause);
    }
}
