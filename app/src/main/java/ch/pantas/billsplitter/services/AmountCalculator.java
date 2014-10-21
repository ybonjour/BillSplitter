package ch.pantas.billsplitter.services;

import java.math.BigDecimal;

public class AmountCalculator {

    public static int convertToCents(String input) {
        if (!isValidAmount(input)) return 0;

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

    public static String convertToString(int amountCents) {
        if(amountCents == 0) return "";
        BigDecimal amount = new BigDecimal(amountCents).divide(new BigDecimal(100));
        return amount.toPlainString();
    }
}
