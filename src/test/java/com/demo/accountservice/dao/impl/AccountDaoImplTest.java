package com.demo.accountservice.dao.impl;

import com.demo.accountservice.domain.Account;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static com.demo.accountservice.dao.impl.AccountDaoImpl.ACCOUNTS_MAP_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AccountDaoImplTest {

    private static final String ACC_NUMBER = "00123";

    private HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

    private AccountDaoImpl accountDao = new AccountDaoImpl(hazelcastInstance);

    @After
    public void cleanup() {
        hazelcastInstance.getMap(ACCOUNTS_MAP_NAME).clear();
    }

    @Test
    public void shouldNotFindAccountInHazelcastMap() {

        Optional<Account> accountOptional = accountDao.findByAccountNumber(ACC_NUMBER);

        assertFalse(accountOptional.isPresent());
    }

    @Test
    public void shouldFindAccountInHazelcastMap() {

        Account account = new Account(ACC_NUMBER, BigDecimal.TEN);

        accountDao.update(account);

        Optional<Account> accountOptional = accountDao.findByAccountNumber(ACC_NUMBER);

        assertTrue(accountOptional.isPresent());
    }

    @Test
    public void shouldUpdateAccount() {

        Account account = new Account(ACC_NUMBER, BigDecimal.TEN);

        accountDao.update(account);

        Optional<Account> accountOptional = accountDao.findByAccountNumber(ACC_NUMBER);
        assertTrue(accountOptional.isPresent());
        assertEquals(BigDecimal.TEN, accountOptional.get().getBalance());

        account.withdraw(BigDecimal.ONE);

        accountDao.update(account);

        accountOptional = accountDao.findByAccountNumber(ACC_NUMBER);
        assertTrue(accountOptional.isPresent());
        assertEquals(9, accountOptional.get().getBalance().intValue());
    }
}