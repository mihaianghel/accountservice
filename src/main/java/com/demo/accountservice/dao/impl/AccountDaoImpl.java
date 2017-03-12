package com.demo.accountservice.dao.impl;

import com.demo.accountservice.dao.AccountDao;
import com.demo.accountservice.domain.Account;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountDaoImpl implements AccountDao {

    protected static final String ACCOUNTS_MAP_NAME = "accounts";
    private HazelcastInstance hazelcastInstance;

    @Autowired
    public AccountDaoImpl(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        Account account =  getMap().get(accountNumber);
        return (account != null) ? Optional.of(account) : Optional.empty();
    }

    @Override
    public void update(Account account) {
        getMap().put(account.getAccountNumber(), account);
    }

    private IMap<String, Account> getMap() {
        return hazelcastInstance.getMap(ACCOUNTS_MAP_NAME);
    }
}
