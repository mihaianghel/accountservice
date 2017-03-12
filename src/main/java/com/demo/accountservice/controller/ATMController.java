package com.demo.accountservice.controller;

import com.demo.accountservice.service.ATMService;
import com.demo.accountservice.service.ValidationService;
import com.demo.accountservice.util.Note;
import com.demo.accountservice.util.ReplenishRequestBody;
import com.demo.accountservice.util.Withdrawal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.demo.accountservice.util.ErrorCode.*;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

@RestController()
@RequestMapping("/atm")
public class ATMController {

    @Autowired
    private ATMService atmService;

    @Autowired
    private ValidationService validationService;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(ATMController.class);

    @GetMapping(value = "/{accountNumber}/balance")
    public ResponseEntity<String> checkBalance(@PathVariable("accountNumber") String accountNumber) {

        Optional<BigDecimal> optionalBalance = atmService.checkBalance(accountNumber);

        if (optionalBalance.isPresent()) {
            return serializeResponseForSuccess(optionalBalance.get());
        } else {
            return status(BAD_REQUEST).body(format(ACCOUNT_UNKNOWN.getMessage(), accountNumber));
        }
    }

    @GetMapping(value = "/{accountNumber}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable("accountNumber") String accountNumber,
                                           @RequestParam("amount") BigDecimal amount) {

        if (!validationService.isValidAmount(amount)) {
            return status(BAD_REQUEST).body(INVALID_AMOUNT.getMessage());
        }

        Withdrawal withdrawal = atmService.withdraw(accountNumber, amount);

        ResponseEntity entity;

        switch (withdrawal.getCode()) {
            case ACCOUNT_UNKNOWN:
                entity = status(BAD_REQUEST).body(format(ACCOUNT_UNKNOWN.getMessage(), accountNumber));
                break;
            case INSUFFICIENT_FUNDS_IN_ACCOUNT:
                entity = status(UNPROCESSABLE_ENTITY).body(format(INSUFFICIENT_FUNDS_IN_ACCOUNT.getMessage(), amount.toString(), accountNumber));
                break;
            case INSUFFICIENT_FUNDS_IN_ATM:
                entity = status(INTERNAL_SERVER_ERROR).body(INSUFFICIENT_FUNDS_IN_ATM.getMessage());
                break;
            default:
                entity = serializeResponseForSuccess(withdrawal);
        }

        return entity;
    }

    @PutMapping(value = "/replenish")
    public ResponseEntity<String> replenish(@RequestBody ReplenishRequestBody requestBody) {

        List<Note> notes = requestBody.getNotes();
        if (validationService.isValidForReplenishment(notes)) {
            atmService.replenish(notes);
            return ResponseEntity.ok("Successfully replenished with notes: " + notes);
        } else {
            return ResponseEntity.status(BAD_REQUEST).body("Invalid notes inserted");
        }
    }

    private ResponseEntity serializeResponseForSuccess(Object object) {
        ResponseEntity entity;
        try {
            entity = ok(MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize response", e);
            entity = status(INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
        return entity;
    }
}
