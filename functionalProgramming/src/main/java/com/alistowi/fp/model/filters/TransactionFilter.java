package com.alistowi.fp.model.filters;

import java.util.ArrayList;
import java.util.Collection;

import com.alistowi.fp.TransactionChecker;
import com.alistowi.fp.model.Transaction;

public class TransactionFilter {
	
	private TransactionFilter() {
		
	}

	public static Collection<Transaction> filterTransactions(Collection<Transaction> transactions,
			TransactionChecker checker) {
		Collection<Transaction> result = new ArrayList<>();
		for (Transaction transaction : transactions) {
			if (checker.check(transaction)) {
				result.add(transaction);
			}
		}
		return result;
	}

}
