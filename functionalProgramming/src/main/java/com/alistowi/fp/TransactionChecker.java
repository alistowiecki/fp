package com.alistowi.fp;

import com.alistowi.fp.model.Transaction;

@FunctionalInterface
public interface TransactionChecker {
	public boolean check(Transaction trade);
}
