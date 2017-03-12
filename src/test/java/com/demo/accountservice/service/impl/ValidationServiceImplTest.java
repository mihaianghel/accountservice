package com.demo.accountservice.service.impl;

import com.demo.accountservice.util.Note;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {

    private ValidationServiceImpl validationService = new ValidationServiceImpl();

    @Test
    public void shouldValidateAmountLessThanAccepted() {

        boolean isValid = validationService.isValidAmount(BigDecimal.TEN);
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateAmountMoreThanAccepted() {

        boolean isValid = validationService.isValidAmount(new BigDecimal(300));
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateAmountNotMultipleOfFive() {

        boolean isValid = validationService.isValidAmount(new BigDecimal(51));
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateAmount() {

        boolean isValid = validationService.isValidAmount(new BigDecimal(50));
        assertTrue(isValid);
    }

    @Test
    public void shouldValidateNotesCorrectDenomination() {
        ReflectionTestUtils.setField(validationService, "allowedDenominations", "5,10,15");
        validationService.init();

        List<Note> notes = Arrays.asList(new Note(5, 2), new Note(7, 1));

        boolean isValid = validationService.isValidForReplenishment(notes);

        assertFalse(isValid);
    }

    @Test
    public void shouldValidateNotesCorrectCount() {
        ReflectionTestUtils.setField(validationService, "allowedDenominations", "5,10,15");
        validationService.init();

        List<Note> notes = Arrays.asList(new Note(5, 2), new Note(15, 0));

        boolean isValid = validationService.isValidForReplenishment(notes);

        assertFalse(isValid);
    }
}