package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;

import static ch.pantas.billsplitter.services.AmountCalculator.*;

public class AmountCalculatorTest extends BaseMockitoInstrumentationTest {

    @SmallTest
    public void testIsValidAmountReturnsTrueForFloatWithPoint(){
        // Given
        String input = "32.20";

        // When
        boolean result = isValidAmount(input);

        // Then
        assertTrue(result);
    }

    @SmallTest
    public void testIsValidAmountReturnsFalseForFloatWithComma(){
        // Given
        String input = "32,20";

        // When
        boolean result = isValidAmount(input);

        // Then
        assertFalse(result);
    }

    @SmallTest
    public void testIsValidAmountReturnsTrueForInteger(){
        // Given
        String input = "32";

        // When
        boolean result = isValidAmount(input);

        // Then
        assertTrue(result);
    }

    @SmallTest
    public void testIsValidAmountReturnsFalseForText(){
        // Given
        String input = "a";

        // When
        boolean result = isValidAmount(input);

        // Then
        assertFalse(result);
    }

    @SmallTest
    public void testConvertToCentsReturns0IfNoValidAmountIsProvided(){
        // Given
        String input = "a";

        // When
        int amountCents = convertToCents(input);

        // Then
        assertEquals(0, amountCents);
    }

    @SmallTest
    public void testConvertToCentsConvertsIntegerAmountCorrectly(){
        // Given
        String input = "32";

        // When
        int amountCents = convertToCents(input);

        // Then
        assertEquals(3200, amountCents);
    }

    @SmallTest
    public void testConvertToCentsConvertsFloatAmountCorrectly(){
        // Given
        String input = "32.35";

        // When
        int amountCents = convertToCents(input);

        // Then
        assertEquals(3235, amountCents);
    }

    @SmallTest
    public void testConvertToCentsFloorsAdditionalNumber(){
        // Given
        String input = "32.356";

        // When
        int amountCents = convertToCents(input);

        // Then
        assertEquals(3235, amountCents);
    }

    @SmallTest
    public void testConvertToCentsDoesNotSufferFromDoubleImprecision(){
        // Due to double imprecision 32.80 might be converted to 3279 in Java
        // This test case insures, that the converter does not suffer from this problem

        // Given
        String input = "32.80";

        // When
        int amountCents = convertToCents(input);

        // Then
        assertEquals(3280, amountCents);
    }

    @SmallTest
    public void testConvertToStringConvertsCorrectlyWithInteger(){
        // Given
        int amountCents = 3200;

        // When
        String output = convertToString(amountCents);

        // Then
        assertEquals("32", output);
    }

    @SmallTest
    public void testConvertToStringConvertsCorrectlyWithFloat(){
        // Given
        int amountCents = 3235;

        // When
        String output = convertToString(amountCents);

        // Then
        assertEquals("32.35", output);
    }

    @SmallTest
    public void testConvertToStringReturnsEmptyStringIfAmountCentsIs0(){
        // Given
        int amountCents = 0;

        // When
        String output = convertToString(amountCents);

        // Then
        assertEquals("", output);
    }
}
