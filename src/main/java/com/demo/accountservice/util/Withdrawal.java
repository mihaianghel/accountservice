package com.demo.accountservice.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.*;

public class Withdrawal {

    private BigDecimal amount;

    private ErrorCode code;

    private List<Note> notes;

    public Withdrawal(BigDecimal amount, ErrorCode code, List<Note> notes) {
        this.amount = amount;
        this.code = code;
        this.notes = notes;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ErrorCode getCode() {
        return code;
    }

    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "{amount=" + amount + ", notes= " + notes + "}";
    }

    public static void main(String[] args) {
        List<Integer> l = Arrays.asList(1,7,8,8);

        System.out.println(l);

        System.out.println(disjunction(l, Arrays.asList(7,8)));
    }
}
