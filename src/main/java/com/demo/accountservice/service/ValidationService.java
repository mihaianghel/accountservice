package com.demo.accountservice.service;

import com.demo.accountservice.util.Note;

import java.math.BigDecimal;
import java.util.List;

public interface ValidationService {

    boolean isValidAmount(BigDecimal amount);

    boolean isValidForReplenishment(List<Note> notes);

}
