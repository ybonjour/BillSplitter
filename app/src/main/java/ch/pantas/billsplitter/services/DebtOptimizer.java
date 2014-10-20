package ch.pantas.billsplitter.services;

import com.google.inject.Singleton;

import java.util.List;

import ch.pantas.billsplitter.model.Debt;

import static com.google.inject.internal.util.$Preconditions.checkNotNull;

@Singleton
public class DebtOptimizer {

    public List<Debt> optimize(List<Debt> debts) {
        checkNotNull(debts);


        // TODO: yb Implement
        return debts;
    }
}
