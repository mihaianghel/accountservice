package com.demo.accountservice.controller;

import com.demo.accountservice.service.ATMService;
import com.demo.accountservice.service.ValidationService;
import com.demo.accountservice.util.Note;
import com.demo.accountservice.util.Withdrawal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.demo.accountservice.util.ErrorCode.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ATMControllerTest {

    private static final String ACCOUNT_NUMBER = "123";
    private static final BigDecimal AMOUNT = BigDecimal.TEN;

    private MockMvc mockMvc;

    @Mock
    private ATMService atmService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ATMController atmController = new ATMController();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(atmController).build();
    }

    @Test
    public void shouldCheckBalanceNonExistingAccount() throws Exception {
        when(atmService.checkBalance(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        mockMvc.perform(get("/atm/123/balance"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account number " + ACCOUNT_NUMBER + " requested does not exist"));
    }

    @Test
    public void shouldCheckBalanceExistingAccount() throws Exception {
        when(atmService.checkBalance(ACCOUNT_NUMBER)).thenReturn(Optional.of(BigDecimal.TEN));

        mockMvc.perform(get("/atm/" + ACCOUNT_NUMBER + "/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    public void shouldTryToWithdrawInvalidAmount() throws Exception {
        when(validationService.isValidAmount(BigDecimal.TEN)).thenReturn(false);

        mockMvc.perform(get("/atm/" + ACCOUNT_NUMBER + "/withdraw?amount=" + AMOUNT.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Amount should be between 20 and 250 in multiples of 5"));
    }

    @Test
    public void shouldTryToWithdrawInvalidAccount() throws Exception {
        when(validationService.isValidAmount(BigDecimal.TEN)).thenReturn(true);
        when(atmService.withdraw(ACCOUNT_NUMBER, AMOUNT))
                .thenReturn(new Withdrawal(AMOUNT, ACCOUNT_UNKNOWN, Collections.emptyList()));

        mockMvc.perform(get("/atm/" + ACCOUNT_NUMBER + "/withdraw?amount=" + AMOUNT.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account number " + ACCOUNT_NUMBER + " requested does not exist"));
    }

    @Test
    public void shouldTryToWithdrawInsufficientFundsInAccounts() throws Exception {
        when(validationService.isValidAmount(BigDecimal.TEN)).thenReturn(true);
        when(atmService.withdraw(ACCOUNT_NUMBER, AMOUNT))
                .thenReturn(new Withdrawal(AMOUNT, INSUFFICIENT_FUNDS_IN_ACCOUNT, Collections.emptyList()));

        mockMvc.perform(get("/atm/" + ACCOUNT_NUMBER + "/withdraw?amount=" + AMOUNT.toString()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Insufficient funds " + AMOUNT.toString() + " in the account number " + ACCOUNT_NUMBER));
    }

    @Test
    public void shouldTryToWithdrawInsufficientFundsInATM() throws Exception {
        when(validationService.isValidAmount(BigDecimal.TEN)).thenReturn(true);
        when(atmService.withdraw(ACCOUNT_NUMBER, AMOUNT))
                .thenReturn(new Withdrawal(AMOUNT, INSUFFICIENT_FUNDS_IN_ATM, Collections.emptyList()));

        mockMvc.perform(get("/atm/" + ACCOUNT_NUMBER + "/withdraw?amount=" + AMOUNT.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Insufficient funds in the ATM"));
    }

    @Test
    public void shouldWithdrawSuccessfully() throws Exception {
        when(validationService.isValidAmount(BigDecimal.TEN)).thenReturn(true);
        when(atmService.withdraw(ACCOUNT_NUMBER, AMOUNT))
                .thenReturn(new Withdrawal(AMOUNT, SUCCESSFUL_WITHDRAWAL, Arrays.asList(new Note(5, 2))));

        mockMvc.perform(get("/atm/" + ACCOUNT_NUMBER + "/withdraw?amount=" + AMOUNT.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"amount\":10,\"code\":\"SUCCESSFUL_WITHDRAWAL\",\"notes\":[{\"denomination\":5,\"count\":2}]}"));
    }

    @Test
    public void shouldReplenish() throws Exception {
        when(validationService.isValidForReplenishment(anyList())).thenReturn(true);

        mockMvc.perform(put("/atm/replenish")
                .content("{\"notes\": [{\"denomination\": 5,\"count\": 10},{\"denomination\": 20,\"count\": 10}]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully replenished with notes: [{denomination=5, count=10}, {denomination=20, count=10}]"));
    }

    @Test
    public void shouldNotReplenish() throws Exception {
        when(validationService.isValidForReplenishment(anyList())).thenReturn(false);

        mockMvc.perform(put("/atm/replenish")
                .content("{\"notes\": [{\"denomination\": 15,\"count\": 10},{\"denomination\": 22,\"count\": 10}]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid notes inserted"));
    }
}