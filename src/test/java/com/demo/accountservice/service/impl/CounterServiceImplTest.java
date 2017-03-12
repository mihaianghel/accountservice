package com.demo.accountservice.service.impl;

import com.demo.accountservice.service.CounterService;
import com.demo.accountservice.util.Count;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CounterServiceImplTest {

    private CounterService counterService = new CounterServiceImpl();

    @Test
    public void shouldCorrectAmountWithSmallestNumberOfNotes() throws Exception {

        Count count = counterService.count(Arrays.asList(30, 20, 10, 5, 5), new BigDecimal(35));

        System.out.println(count);

        assertEquals(new BigDecimal(35).intValue(), count.getSum());
        assertEquals(2, count.getValues().size());
        assertTrue(count.getValues().contains(5));
        assertTrue(count.getValues().contains(30));
    }
}