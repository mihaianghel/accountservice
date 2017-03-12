package com.demo.accountservice.service;

import com.demo.accountservice.util.Note;
import com.demo.accountservice.util.Withdrawal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ATMService {

    void replenish(List<Note> notes);

    List<Integer> getAvailableNotes();

    Optional<BigDecimal> checkBalance(String accountNumber);

    Withdrawal withdraw(String accountNumber, BigDecimal amount);
}
