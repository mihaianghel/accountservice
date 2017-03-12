package com.demo.accountservice.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class Account implements Serializable {

    private String accountNumber;
    private BigDecimal balance;

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {

        if (amount.compareTo(balance) > 0) {
            return BigDecimal.ZERO;
        } else {
            this.balance = this.balance.subtract(amount);
            return amount;
        }
    }
}
