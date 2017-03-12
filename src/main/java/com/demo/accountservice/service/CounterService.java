package com.demo.accountservice.service;

import com.demo.accountservice.util.Count;

import java.math.BigDecimal;
import java.util.List;

public interface CounterService {

    Count count(List<Integer> notes, BigDecimal amount);
}
