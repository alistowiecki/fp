package com.alistowi.fp.model.filters;

import java.util.ArrayList;
import java.util.Collection;

import com.alistowi.fp.TransactionChecker;
import com.alistowi.fp.model.Transaction;

public class PartialApplicationTransactionFilter {

	private Collection<Transaction> transactions;

	private TransactionChecker checker;

	public PartialApplicationTransactionFilter(Collection<Transaction> transactions) {
		this.transactions = transactions;
	}

	public PartialApplicationTransactionFilter filterTransactions(TransactionChecker checker) {
		this.checker = checker;
		return this;
	}

	public Collection<Transaction> collect() {
		Collection<Transaction> result = new ArrayList<>();
		for (Transaction transaction : transactions) {
			if (checker.check(transaction)) {
				result.add(transaction);
			}
		}
		return result;
	}

}
