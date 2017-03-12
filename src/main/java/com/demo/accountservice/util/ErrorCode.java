package com.demo.accountservice.util;

public enum ErrorCode {

    ACCOUNT_UNKNOWN ("Account number %s requested does not exist"),
    INSUFFICIENT_FUNDS_IN_ACCOUNT ("Insufficient funds %s in the account number %s"),
    INSUFFICIENT_FUNDS_IN_ATM ("Insufficient funds in the ATM"),
    INVALID_AMOUNT ("Amount should be between 20 and 250 in multiples of 5"),
    SUCCESSFUL_WITHDRAWAL ("Successful withdrawal %s from account account %s");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
