package com.alistowi.fp;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import com.alistowi.fp.model.Chargeline;
import com.alistowi.fp.model.Transaction;
import com.alistowi.fp.model.TransactionType;
import com.alistowi.fp.model.filters.PartialApplicationTransactionFilter;
import com.alistowi.fp.model.filters.TransactionFilter;

public class FunctionalProgrammingTest {

	/**
	 * Find all transactions done by Zbyszek.
	 */
	@Test
	public void testFilter() {
		List<Transaction> transactions = createTransactions();

		Collection<Transaction> transactionsDoneByZbyszek = new PartialApplicationTransactionFilter(transactions)
				.filterTransactions(doneByZbyszek()).collect();

		assertEquals(1, transactionsDoneByZbyszek.size());
		assertEquals("Zbyszek", transactionsDoneByZbyszek.iterator().next().getIssuer());
	}

	/**
	 * Find all buy transactions.
	 */
	@Test
	public void testFilter2() {
		List<Transaction> transactions = createTransactions();

		Collection<Transaction> buyTransactions = new PartialApplicationTransactionFilter(transactions)
				.filterTransactions(Transaction::isBuy).collect();
		assertEquals(2, buyTransactions.size());
	}

	/**
	 * Find all buy transactions.
	 */
	@Test
	public void testFilterCurrying() {
		List<Transaction> transactions = createTransactions();
		BiFunction<Collection<Transaction>, TransactionChecker, Collection<Transaction>> filterTransactions = TransactionFilter::filterTransactions;
		Collection<Transaction> transactionsDoneByZbyszek = filterTransactions.apply(transactions, doneByZbyszek());
		
		assertEquals(1, transactionsDoneByZbyszek.size());
		assertEquals("Zbyszek", transactionsDoneByZbyszek.iterator().next().getIssuer());

		Function<Collection<Transaction>, Function<TransactionChecker, Collection<Transaction>>> filterTransactionsCurrying = (
				Collection<Transaction> q) -> (TransactionChecker e) -> filterTransactions.apply(q, e);
		Collection<Transaction> transactionsDoneByZbyszekWithCurrying = filterTransactionsCurrying.apply(transactions)
				.apply(doneByZbyszek());
		
		assertEquals(1, transactionsDoneByZbyszekWithCurrying.size());
		assertEquals("Zbyszek", transactionsDoneByZbyszekWithCurrying.iterator().next().getIssuer());
	}

	private TransactionChecker doneByZbyszek() {
		return transaction -> transaction.getIssuer().equals("Zbyszek");
	}

	/**
	 * Divide function and inverse function.
	 */
	@Test
	public void testCurrying() {
		BiFunction<Double, Double, Double> divide = (a, b) -> a / b;
		System.out.println(divide.apply(1.0, 4.0));
		Function<Double, Function<Double, Double>> curryiedDivide = (Double a) -> (Double b) -> divide.apply(a, b);
		Function<Double, Double> inverse = curryiedDivide.apply(1.0);
		System.out.println(inverse.apply(4.0));
	}

	/**
	 * Find all buy transactions.
	 */
	@Test
	public void testPartialApplicationFilter() {
		List<Transaction> transactions = createTransactions();

		Collection<Transaction> buyTransactions = new PartialApplicationTransactionFilter(transactions)
				.filterTransactions(Transaction::isBuy).collect();
		assertEquals(2, buyTransactions.size());
	}

	/**
	 * Method references can have different functional interface signature.
	 */
	@Test
	public void testMethodReferences() {
		Transaction zbyszekBuyBike = createTransactions().get(0);

		Function<Transaction, BigDecimal> getTotalAmountOnClass = Transaction::getTotalAmount;
		System.out.println(getTotalAmountOnClass.apply(zbyszekBuyBike));

		Supplier<BigDecimal> getTotalAmountOnInstance = zbyszekBuyBike::getTotalAmount;
		System.out.println(getTotalAmountOnInstance.get());
	}

	/**
	 * Constructor references can have different functional interface signature.
	 */
	@Test
	public void testConstructorReference() {
		Supplier<String> emptyStringSupplier = String::new;
		System.out.println(emptyStringSupplier.get());

		Function<char[], String> stringCreator = String::new;
		char[] characters = { 'R', 'o', 'm', 'e', 'k' };
		System.out.println(stringCreator.apply(characters));
	}

	private List<Transaction> createTransactions() {
		List<Transaction> transactions = Arrays.asList(
				new Transaction("Zbyszek", TransactionType.COST,
						Collections.singletonList(new Chargeline(BigDecimal.TEN, "rower", 5))),
				new Transaction("Romek", TransactionType.INCOME,
						Collections.singletonList(new Chargeline(BigDecimal.TEN, "rolki", 7))),
				new Transaction("Tomek", TransactionType.COST,
						Collections.singletonList(new Chargeline(BigDecimal.TEN, "deskorolka", 11))));
		return transactions;
	}

}
