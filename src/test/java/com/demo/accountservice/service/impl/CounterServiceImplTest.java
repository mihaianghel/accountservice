package com.demo.accountservice.service.impl;

import com.demo.accountservice.service.CounterService;
import com.demo.accountservice.util.Count;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class CounterServiceImplTest {

    private CounterService counterService = new CounterServiceImpl();

    @Test
    public void shouldCorrectAmountWithSmallestNumberOfNotes() throws Exception {

        Count count = counterService.count(Arrays.asList(5,5,20,20,10,10,10,30), new BigDecimal(80));

        System.out.println(count);

        assertEquals(new BigDecimal(80).intValue(), count.getSum());
        assertEquals(5, count.getValues().size());
        assertEquals(1, Collections.frequency(count.getValues(), 30));
        assertEquals(2, Collections.frequency(count.getValues(), 20));
        assertEquals(2, Collections.frequency(count.getValues(), 5));
    }
}