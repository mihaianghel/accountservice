package com.demo.accountservice.service.impl;

import com.demo.accountservice.dao.AccountDao;
import com.demo.accountservice.domain.Account;
import com.demo.accountservice.service.AccountService;
import com.demo.accountservice.util.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountDao accountDao;

    @Override
    public Optional<BigDecimal> checkBalance(String accountNumber) {

        Optional<Account> optionalAccount = accountDao.findByAccountNumber(accountNumber);

        if (optionalAccount.isPresent()) {
            return Optional.of(optionalAccount.get().getBalance());
        } else {
            LOGGER.error(String.format(ErrorCode.ACCOUNT_UNKNOWN.getMessage(), accountNumber));
            return Optional.empty();
        }
    }

    @Override
    public Optional<BigDecimal> withdraw(String accountNumber, BigDecimal amount) {

        Optional<Account> optionalAccount = accountDao.findByAccountNumber(accountNumber);

        if (optionalAccount.isPresent()) {

            Account account = optionalAccount.get();

            BigDecimal amountToWithdraw = account.withdraw(amount);
            updateAccount(account, amountToWithdraw);

            return Optional.of(amountToWithdraw);
        } else {
            LOGGER.error(String.format(ErrorCode.ACCOUNT_UNKNOWN.getMessage(), accountNumber));
            return Optional.empty();
        }
    }

    private void updateAccount(Account account, BigDecimal amountToWithdraw) {
        if (amountToWithdraw.compareTo(BigDecimal.ZERO) > 0) {
            accountDao.update(account);
        } else {
            LOGGER.error(String.format(ErrorCode.INSUFFICIENT_FUNDS_IN_ACCOUNT.getMessage(),
                    amountToWithdraw.toString(), account.getAccountNumber()));
        }
    }
}
