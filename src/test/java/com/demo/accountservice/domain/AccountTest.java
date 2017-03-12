package com.demo.accountservice.domain;

import org.junit.Test;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AccountTest {

    @Test
    public void shouldWithdraw() {
        Account account = new Account("1", BigDecimal.TEN);

        account.withdraw(BigDecimal.ONE);

        assertEquals(new BigDecimal(9), account.getBalance());
    }

    @Test
    public void shouldNotWithdraw() {
        Account account = new Account("1", BigDecimal.TEN);

        account.withdraw(new BigDecimal(15));

        assertEquals(BigDecimal.TEN, account.getBalance());
    }

}