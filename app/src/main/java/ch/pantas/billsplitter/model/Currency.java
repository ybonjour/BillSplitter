package ch.pantas.billsplitter.model;

public enum Currency {
    CHF("%.2f CHF"), EUR("%.2f EUR"), USD("$ %.2f");

    private final String formatTemplate;

    Currency(String formatTemplate) {
        this.formatTemplate = formatTemplate;
    }

    public String format(int amountCents){
        double amount = amountCents / 100;

        return String.format(formatTemplate, amount);
    }
}
