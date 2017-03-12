package com.demo.accountservice.service;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {

    Optional<BigDecimal> checkBalance(String accountNumber);

    Optional<BigDecimal> withdraw(String accountNumber, BigDecimal amount);
}
