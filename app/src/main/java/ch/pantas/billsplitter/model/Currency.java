package ch.pantas.billsplitter.model;

import static ch.pantas.billsplitter.services.AmountCalculator.convertToString;

public enum Currency {
    CHF("CHF", "%s CHF"), EUR("EUR", "%s EUR"), USD("$", "$ %s");

    private final String symbol;
    private final String formatTemplate;

    Currency(String symbol, String formatTemplate) {
        this.symbol = symbol;
        this.formatTemplate = formatTemplate;
    }

    public String format(int amountCents) {
        return String.format(formatTemplate, convertToString(amountCents));
    }

    public String getSymbol() {
        return symbol;
    }
}
