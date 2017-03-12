package com.demo.accountservice.service.impl;

import com.demo.accountservice.util.Count;
import com.demo.accountservice.service.ATMService;
import com.demo.accountservice.service.AccountService;
import com.demo.accountservice.service.CounterService;
import com.demo.accountservice.util.Note;
import com.demo.accountservice.util.Withdrawal;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static com.demo.accountservice.util.ErrorCode.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.*;

@Service
public class ATMServiceImpl implements ATMService {

    private static Collection<Integer> NOTES = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger( ATMServiceImpl.class );

    @Autowired
    private AccountService accountService;

    @Autowired
    private CounterService counterService;

    @Value("${notes}")
    private String notes;

    @PostConstruct
    public void init() {
        NOTES.addAll( Arrays.asList( notes.split( "," ) )
                .stream()
                .map( n -> Integer.valueOf( n ) )
                .collect( toList() ) );
    }

    @Override
    public void replenish(List<Note> notes) {

        List<Integer> allNotes = new ArrayList<>();

        notes.stream().forEach( n -> IntStream.rangeClosed( 1, n.getCount() )
                .forEach( i -> allNotes.add( n.getDenomination() ) ) );

        NOTES.addAll( allNotes );
        LOGGER.info( "Added the following amount in the ATM: " + allNotes.stream().mapToInt( Integer::intValue ).sum() );
    }

    @Override
    public List<Integer> getAvailableNotes() {
        return new ArrayList<>( NOTES );
    }

    @Override
    public Optional<BigDecimal> checkBalance(String accountNumber) {
        return accountService.checkBalance( accountNumber );
    }

    @Override
    public Withdrawal withdraw(String accountNumber, BigDecimal amount) {

        Withdrawal withdrawal;

        Count count = counterService.count( new ArrayList<>( NOTES ), amount );

        if (canDisburseRequestedAmount(amount, count)) {

            Optional<BigDecimal> optionalAmountToWithdraw = accountService.withdraw( accountNumber, amount );

            if (optionalAmountToWithdraw.isPresent()) {

                BigDecimal amountToWithdraw = optionalAmountToWithdraw.get();

                if (!amountToWithdraw.equals( BigDecimal.ZERO )) {

                    NOTES = CollectionUtils.disjunction( NOTES, count.getValues() );

                    withdrawal = new Withdrawal( amount, SUCCESSFUL_WITHDRAWAL, collectDenominations( count.getValues() ) );
                    LOGGER.info( String.format( SUCCESSFUL_WITHDRAWAL.getMessage(), amount.toString(), accountNumber ) );
                } else {
                    withdrawal = new Withdrawal( BigDecimal.ZERO, INSUFFICIENT_FUNDS_IN_ACCOUNT, Collections.emptyList() );
                    LOGGER.error( format( INSUFFICIENT_FUNDS_IN_ACCOUNT.getMessage(), amount.toString(), accountNumber ) );
                }
            } else {
                withdrawal = new Withdrawal( BigDecimal.ZERO, ACCOUNT_UNKNOWN, Collections.emptyList() );
                LOGGER.error( format( ACCOUNT_UNKNOWN.getMessage(), accountNumber ) );
            }
        } else {
            withdrawal = new Withdrawal( BigDecimal.ZERO, INSUFFICIENT_FUNDS_IN_ATM, Collections.emptyList() );
            LOGGER.error( INSUFFICIENT_FUNDS_IN_ATM.getMessage() );
        }

        return withdrawal;
    }

    private List<Note> collectDenominations(List<Integer> values) {
        List<Note> notes = new ArrayList<>();

        int fiveFrequency = Collections.frequency( values, 5 );
        int tenFrequency = Collections.frequency( values, 10 );
        int twentyFrequency = Collections.frequency( values, 20 );
        int fiftyFrequency = Collections.frequency( values, 50 );

        if (fiveFrequency > 0) notes.add( new Note( 5, fiveFrequency ) );
        if (tenFrequency > 0) notes.add( new Note( 10, tenFrequency ) );
        if (twentyFrequency > 0) notes.add( new Note( 20, twentyFrequency ) );
        if (fiftyFrequency > 0) notes.add( new Note( 50, fiftyFrequency ) );

        return notes;
    }

    private boolean canDisburseRequestedAmount(BigDecimal amount, Count count) {
        return count.getSum() == amount.intValue();
    }
}
