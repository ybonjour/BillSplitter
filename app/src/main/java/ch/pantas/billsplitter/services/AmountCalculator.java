package ch.pantas.billsplitter.services;

import java.math.BigDecimal;

import ch.pantas.billsplitter.model.Currency;

import static com.google.inject.internal.util.$Preconditions.checkArgument;

public class AmountCalculator {

    public static int convertToCents(String input) {
        checkArgument(isValidAmount(input));

        BigDecimal amount = new BigDecimal(input);
        return amount.multiply(new BigDecimal(100)).intValue();
    }

    public static boolean isValidAmount(String input) {
        try {
            new BigDecimal(input);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String convertToString(int amountCents){
        BigDecimal amount = new BigDecimal(amountCents).divide(new BigDecimal(100));
        return amount.toPlainString();
    }
}
