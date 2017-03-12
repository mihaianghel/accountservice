package com.demo.accountservice.service.impl;

import com.demo.accountservice.service.AccountService;
import com.demo.accountservice.service.CounterService;
import com.demo.accountservice.util.Count;
import com.demo.accountservice.util.ErrorCode;
import com.demo.accountservice.util.Note;
import com.demo.accountservice.util.Withdrawal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ATMServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private CounterService counterService;

    @InjectMocks
    private ATMServiceImpl atmService = new ATMServiceImpl();

    @Test
    public void shouldReplenish() {

        ReflectionTestUtils.setField(atmService, "notes", "1,2,3,4");
        atmService.init();
        List<Integer> notesBeforeReplenish = atmService.getAvailableNotes();

        Note note = new Note(30, 3);
        atmService.replenish(Arrays.asList(note));

        List<Integer> notesAfterReplenish = atmService.getAvailableNotes();

        assertEquals(4, notesBeforeReplenish.size());
        assertEquals(7, notesAfterReplenish.size());
    }

    @Test
    public void shouldCheckAvailableBalance() {

        String accNumber = "00123";

        atmService.checkBalance(accNumber);

        verify(accountService).checkBalance(accNumber);
    }

    @Test
    public void shouldNotFindAccountForWithdrawal() {

        String accNumber = "00123";
        BigDecimal amount = BigDecimal.TEN;

        when(accountService.withdraw(accNumber, amount)).thenReturn(Optional.empty());

        Withdrawal withdrawal = atmService.withdraw(accNumber, amount);

        assertTrue(withdrawal.getCode().equals(ErrorCode.ACCOUNT_UNKNOWN));
    }

    @Test
    public void shouldNotHaveEnoughFundsInTheAccount() {

        String accNumber = "00123";
        BigDecimal amount = BigDecimal.TEN;

        when(accountService.withdraw(accNumber, amount)).thenReturn(Optional.of(BigDecimal.ZERO));

        Withdrawal withdrawal = atmService.withdraw(accNumber, amount);

        assertTrue(withdrawal.getCode().equals(ErrorCode.INSUFFICIENT_FUNDS_IN_ACCOUNT));
    }

    @Test
    public void shouldNotHaveEnoughFundsInTheATM() {

        String accNumber = "00123";
        BigDecimal amount = BigDecimal.TEN;

        when(accountService.withdraw(accNumber, amount)).thenReturn(Optional.of(BigDecimal.TEN));
        when(counterService.count(atmService.getAvailableNotes(), amount)).thenReturn(new Count(1, Collections.emptyList()));

        Withdrawal withdrawal = atmService.withdraw(accNumber, amount);

        assertTrue(withdrawal.getCode().equals(ErrorCode.INSUFFICIENT_FUNDS_IN_ATM));
    }

    @Test
    public void shouldWithdrawSuccessfully() {

        String accNumber = "00123";
        BigDecimal amount = BigDecimal.TEN;

        when(accountService.withdraw(accNumber, amount)).thenReturn(Optional.of(amount));
        when(counterService.count(atmService.getAvailableNotes(), amount)).thenReturn(new Count(amount.intValue(), Arrays.asList(5,5)));

        Withdrawal withdrawal = atmService.withdraw(accNumber, amount);

        assertTrue(withdrawal.getAmount().equals(BigDecimal.TEN));
        assertTrue(withdrawal.getCode().equals(ErrorCode.SUCCESSFUL_WITHDRAWAL));
    }

}