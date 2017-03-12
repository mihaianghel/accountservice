package com.demo.accountservice.service.impl;

import com.demo.accountservice.dao.AccountDao;
import com.demo.accountservice.domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

    private static final String ACC_NUMBER = "00123";
    private static final BigDecimal AMOUNT = BigDecimal.TEN;

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl accountService = new AccountServiceImpl();

    @Test
    public void shouldCheckBalanceForNonExistingAccount() {

        when(accountDao.findByAccountNumber(ACC_NUMBER)).thenReturn(Optional.empty());

        Optional<BigDecimal> balance = accountService.checkBalance(ACC_NUMBER);

        assertFalse(balance.isPresent());
    }

    @Test
    public void shouldCheckBalanceForExistingAccount() {

        when(accountDao.findByAccountNumber(ACC_NUMBER)).thenReturn(Optional.of(new Account(ACC_NUMBER, BigDecimal.TEN)));

        Optional<BigDecimal> balance = accountService.checkBalance(ACC_NUMBER);

        assertTrue(balance.isPresent());
        assertEquals(BigDecimal.TEN, balance.get());
    }

    @Test
    public void shouldNotWithdrawForNonExistingAccount() {

        when(accountDao.findByAccountNumber(ACC_NUMBER)).thenReturn(Optional.empty());

        Optional<BigDecimal> balance = accountService.withdraw(ACC_NUMBER, AMOUNT);

        assertFalse(balance.isPresent());
    }

    @Test
    public void shouldNotHaveEnoughFundsToWithdraw() {

        when(accountDao.findByAccountNumber(ACC_NUMBER)).thenReturn(Optional.of(new Account(ACC_NUMBER, BigDecimal.ONE)));

        Optional<BigDecimal> balance = accountService.withdraw(ACC_NUMBER, AMOUNT);

        assertTrue(balance.isPresent());
        assertEquals(BigDecimal.ZERO, balance.get());
    }

    @Test
    public void shouldWithdraw() {

        when(accountDao.findByAccountNumber(ACC_NUMBER)).thenReturn(Optional.of(new Account(ACC_NUMBER, BigDecimal.TEN)));

        Optional<BigDecimal> balance = accountService.withdraw(ACC_NUMBER, AMOUNT);

        assertTrue(balance.isPresent());
        assertEquals(BigDecimal.TEN, balance.get());
    }
}