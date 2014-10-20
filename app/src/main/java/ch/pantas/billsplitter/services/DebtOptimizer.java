package ch.pantas.billsplitter.services;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.pantas.billsplitter.model.Debt;
import ch.pantas.billsplitter.model.User;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static com.google.inject.internal.util.$Preconditions.checkState;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

@Singleton
public class DebtOptimizer {


    public List<Debt> optimize(List<Debt> debts) {
        checkNotNull(debts);

        Map<User, Integer> balances = buildBalance(debts);

        List<Balance> positives = extractReverseSortedPositiveBalances(balances);
        List<Balance> negatives = extractReverseSortedNegativeBalances(balances);

        List<Debt> result = new LinkedList<Debt>();

        while (!positives.isEmpty()) {

            Balance positive = pop(positives);

            int debtAmount;
            Balance negativeStar = findBestNegativeFit(positive, negatives);
            if (negativeStar != null) {
                debtAmount = negativeStar.getAmount();
                positive.decrease(negativeStar.getAmount());

                addIfNeeded(positives, positive);
                negatives.remove(negativeStar);
            } else {
                negativeStar = pop(negatives);
                debtAmount = positive.getAmount();
                negativeStar.decrease(positive.getAmount());

                addIfNeeded(negatives, negativeStar);
            }

            result.add(new Debt(negativeStar.getUser(), positive.getUser(), debtAmount));
        }

        // Verify that the optimization di not change the balances of the users
        assertBalancesAreEqual(balances, buildBalance(result));

        return result;
    }

    private static Balance pop(List<Balance> balances){
        Balance balance = balances.get(0);
        balances.remove(0);

        return balance;
    }

    private static void addIfNeeded(List<Balance> balances, Balance balance){
        if(balance.getAmount() > 0) {
            balances.add(balance);
            sort(balances, reverseOrder());
        }
    }

    private Balance findBestNegativeFit(Balance positive, List<Balance> negatives) {

        for (Balance negative : negatives) {
            if (negative.getAmount() <= positive.getAmount()) {
                return negative;
            }
        }

        return null;
    }


    private List<Balance> extractReverseSortedPositiveBalances(Map<User, Integer> balances) {
        List<Balance> positiveBalances = new ArrayList<Balance>();
        for (User user : balances.keySet()) {
            int amount = balances.get(user);
            if (amount > 0) {
                positiveBalances.add(new Balance(user, amount));
            }
        }

        sort(positiveBalances, reverseOrder());
        return positiveBalances;
    }

    private List<Balance> extractReverseSortedNegativeBalances(Map<User, Integer> balances) {
        List<Balance> negativeBalances = new ArrayList<Balance>();
        for (User user : balances.keySet()) {
            int amount = balances.get(user);
            if (amount < 0) {
                negativeBalances.add(new Balance(user, -1 * amount));
            }
        }

        sort(negativeBalances, reverseOrder());
        return negativeBalances;
    }

    private Map<User, Integer> buildBalance(List<Debt> debts) {
        Map<User, Integer> balance = new HashMap<User, Integer>();

        for (Debt debt : debts) {
            User from = debt.getFrom();
            if (!balance.containsKey(from)) {
                balance.put(from, 0);
            }

            User to = debt.getTo();
            if (!balance.containsKey(to)) {
                balance.put(to, 0);
            }


            balance.put(to, balance.get(to) + debt.getAmount());
            balance.put(from, balance.get(from) - debt.getAmount());
        }

        return balance;
    }

    private void assertBalancesAreEqual(Map<User, Integer> balancesBefore, Map<User, Integer> balancesAfter) {
        // balancesBefore might contain elements with balance 0. These do not exist in balanceAfter
        checkState(balancesBefore.size() >= balancesAfter.size());

        for(User user : balancesBefore.keySet()) {
            if(balancesAfter.containsKey(user)){
                checkState(balancesBefore.get(user).equals(balancesAfter.get(user)), user.getName() + ": " + balancesBefore.get(user) + " != " + balancesAfter.get(user));
            } else {
                checkState(balancesBefore.get(user).equals(0), user.getName() + ": balance != 0");
            }
        }
    }

    private class Balance implements Comparable<Balance> {
        private final User user;
        private Integer amount = 0;

        private Balance(User user, int amount) {
            this.user = user;
            this.amount = amount;
        }

        public User getUser() {
            return user;
        }

        public int getAmount() {
            return amount;
        }

        public void decrease(int amount) {
            this.amount -= amount;
        }

        @Override
        public int compareTo(Balance balance) {
            if (balance == null) return 1;

            return this.amount.compareTo(balance.getAmount());
        }
    }
}
