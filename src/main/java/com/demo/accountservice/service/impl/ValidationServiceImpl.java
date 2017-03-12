package com.demo.accountservice.service.impl;

import com.demo.accountservice.controller.ATMController;
import com.demo.accountservice.service.ValidationService;
import com.demo.accountservice.util.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ValidationServiceImpl implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ATMController.class);
    private static List<Integer> ALLOWED_DENOMINATIONS;

    @Value("${allowedDenominations}")
    private String allowedDenominations;

    @PostConstruct
    public void init() {
        ALLOWED_DENOMINATIONS = Arrays.asList(allowedDenominations.split(","))
                .stream()
                .map(n -> Integer.valueOf(n))
                .collect(toList());
    }

    @Override
    public boolean isValidAmount(BigDecimal amount) {
        return (amount.compareTo(new BigDecimal(20)) >= 0)
                && (amount.compareTo(new BigDecimal(250)) <= 0)
                && (amount.remainder(new BigDecimal(5)).compareTo(BigDecimal.ZERO) == 0);
    }

    @Override
    public boolean isValidForReplenishment(List<Note> notes) {



        if (!notes.isEmpty()) {
            return notes.stream()
                    .allMatch(n ->  ALLOWED_DENOMINATIONS.contains(n.getDenomination()) && n.getCount() > 0);
        } else {
            LOGGER.error("Missing notes");
            return false;
        }
    }
}
