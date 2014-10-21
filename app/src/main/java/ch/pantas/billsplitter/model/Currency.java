package ch.pantas.billsplitter.model;

import java.util.ArrayList;

import static ch.pantas.billsplitter.services.AmountCalculator.convertToString;

public enum Currency {
    CHF("CHF", "%s CHF"), EUR("€", "%s €"), USD("$", "$ %s");

    private final String symbol;
    private final String formatTemplate;

    Currency(String symbol, String formatTemplate) {
        this.symbol = symbol;
        this.formatTemplate = formatTemplate;
    }

    public String format(int amountCents) {
        String amount = convertToString(amountCents);
        if(amount.isEmpty()) return "";
        return String.format(formatTemplate, amount);
    }

    public String getSymbol() {
        return symbol;
    }

    public static String[] getValuesAsString(){
        ArrayList<String> symbols = new ArrayList<String>();
        for(Currency c : values()){
            symbols.add(c.toString());
        }
        String[] buffer = new String[symbols.size()];
        return symbols.toArray(buffer);
    }
}
