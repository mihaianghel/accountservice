package com.demo.accountservice.dao;

import com.demo.accountservice.domain.Account;

import java.util.Optional;

public interface AccountDao {

    Optional<Account> findByAccountNumber(String accountNumber);

    void update(Account account);
}
