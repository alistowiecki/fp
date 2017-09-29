package com.alistowi.fp;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

import com.alistowi.fp.model.Chargeline;
import com.alistowi.fp.model.Transaction;
import com.alistowi.fp.model.TransactionSize;
import com.alistowi.fp.model.TransactionType;

public class StreamsTest {

	/**
	 * Filter all buy transactions.
	 */
	@Test
	public void testStreamFilter() {
		List<Transaction> transactions = createTransactions();
		
		List<Transaction> buyTransactions = transactions.stream().filter(Transaction::isBuy).collect(toList());
		
		assertEquals(2, buyTransactions.size());
	}
	
	/**
	 * Filter all buy transactions done by Andrzej and with total amount between
	 * one and a hundred
	 */
	@Test
	public void testHardStreamFilter() {
		List<Transaction> transactions = createTransactions();

		List<Transaction> buyTransactionsDoneByAndrzejWithTotalAmountBetweenOneAndAHundred = transactions.stream()
				.filter(t -> t.isBuy() && t.getIssuer().equals("Andrzej")
						&& t.getTotalAmount().compareTo(BigDecimal.ONE) == 1
						&& t.getTotalAmount().compareTo(BigDecimal.valueOf(100)) == -1)
				.collect(toList());

		Stream<Transaction> transactionsWithAmountBetweenOneAndAHundred = transactions.stream()
				.filter(t -> isAmountBeetween(t.getTotalAmount(), BigDecimal.ONE, BigDecimal.valueOf(100)));
		buyTransactionsDoneByAndrzejWithTotalAmountBetweenOneAndAHundred = transactionsWithAmountBetweenOneAndAHundred
				.filter(Transaction::isBuy).filter(this::isIssuerAndrzej).collect(toList());

		assertEquals(1, buyTransactionsDoneByAndrzejWithTotalAmountBetweenOneAndAHundred.size());
		assertEquals("Andrzej",
				buyTransactionsDoneByAndrzejWithTotalAmountBetweenOneAndAHundred.iterator().next().getIssuer());
	}

	private boolean isAmountBeetween(BigDecimal amount, BigDecimal lower, BigDecimal higher) {
		return amount.compareTo(lower) == 1 && amount.compareTo(higher) == -1;
	}

	private boolean isIssuerAndrzej(Transaction t) {
		return t.getIssuer().equals("Andrzej");
	}

	/**
	 * Find issuer names.
	 */
	@Test
	public void testMap() {
		List<Transaction> transactions = createTransactions();

		Set<String> issuerNames = transactions.stream().map(Transaction::getIssuer).collect(toSet());

		assertEquals(6, issuerNames.size());
		assertTrue(issuerNames.contains("Andrzej"));
	}

	/**
	 * Find summary amount for all transactions.
	 */
	@Test
	public void testMapReduce() {
		List<Transaction> transactions = createTransactions();
		
		BigDecimal summaryAmountForAllTransactions = transactions.stream().map(Transaction::getTotalAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		summaryAmountForAllTransactions = transactions.stream().map(Transaction::getTotalAmount).reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
		
		assertEquals(BigDecimal.valueOf(62), summaryAmountForAllTransactions);
	}

	/**
	 * Find summary amount for all transactions (do not use total amount).
	 */
	@Test
	public void testFlatMap() {
		List<Transaction> transactions = createTransactions();
		
		BigDecimal summaryAmountForAllTransactions = transactions.stream().flatMap(t -> t.getChargelines().stream())
				.map(Chargeline::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
		
		assertEquals(BigDecimal.valueOf(62), summaryAmountForAllTransactions);
	}


	/**
	 * Find any buy transactions.
	 */
	@Test
	public void testFind() {
		List<Transaction> transactions = createTransactions();

		Transaction buyTransaction = transactions.stream().filter(Transaction::isBuy).findAny().orElse(null);

		assertNotNull(buyTransaction);
	}

	/**
	 * Find transaction with highest total amount.
	 */
	@Test
	public void testComparing() {
		List<Transaction> transactions = createTransactions();

		Optional<Transaction> transactionWithMaxTotalAmount = transactions.stream()
				.max((t1, t2) -> t1.getTotalAmount().compareTo(t2.getTotalAmount()));

		transactionWithMaxTotalAmount = transactions.stream().max(comparing(Transaction::getTotalAmount));

		assertEquals(BigDecimal.valueOf(20), transactionWithMaxTotalAmount.get().getTotalAmount());
	}

	/**
	 * Group transaction by issuer.
	 */
	@Test
	public void testGrouping() {
		List<Transaction> transactions = createTransactions();

		Map<String, List<Transaction>> transactionsByIssuer = transactions.stream()
				.collect(groupingBy(Transaction::getIssuer));

		assertEquals(2, transactionsByIssuer.get("Zbyszek").size());
	}

	/**
	 * Group transaction by issuer and then by type.
	 */
	@Test
	public void testGroupingSecondLevel() {
		List<Transaction> transactions = createTransactions();

		Map<String, Map<TransactionType, List<Transaction>>> transactionsByIssuerAndType = transactions.stream()
				.collect(groupingBy(Transaction::getIssuer, groupingBy(Transaction::getType)));

		assertEquals(1, transactionsByIssuerAndType.get("Zbyszek").size());
		assertEquals(2, transactionsByIssuerAndType.get("Zbyszek").get(TransactionType.INCOME).size());
	}

	/**
	 * How many transactions made Zbyszek?
	 */
	@Test
	public void testGroupingAndCount() {
		List<Transaction> transactions = createTransactions();

		Long zbyszekTransactionsCount = transactions.stream().collect(groupingBy(Transaction::getIssuer, counting()))
				.get("Zbyszek");

		assertEquals(2L, zbyszekTransactionsCount.longValue());
	}

	/**
	 * Find transaction with maximum total amount done for each Issuer.
	 */
	@Test
	public void testGroupingAndMax() {
		List<Transaction> transactions = createTransactions();

		@SuppressWarnings("unused")
		Map<String, Optional<Transaction>> biggestTransactionByIssuerTemp = transactions.stream()
				.collect(groupingBy(Transaction::getIssuer, maxBy(comparing(Transaction::getTotalAmount))));

		Map<String, Transaction> biggestTransactionByIssuer = transactions.stream()
				.collect(groupingBy(Transaction::getIssuer,
						collectingAndThen(maxBy(comparing(Transaction::getTotalAmount)), Optional::get)));

		assertEquals(BigDecimal.TEN, biggestTransactionByIssuer.get("Zbyszek").getTotalAmount());
		assertEquals(BigDecimal.valueOf(20), biggestTransactionByIssuer.get("Andrzej").getTotalAmount());
	}

	/**
	 * Group Issuer to this, who do BIG Transactions and SMALL.
	 */
	@Test
	public void testGroupingAndMapping() {
		List<Transaction> transactions = createTransactions();

		Map<TransactionSize, Set<String>> transactionSize2Issuer = transactions.stream()
				.collect(groupingBy(transaction -> {
					if (transaction.getTotalAmount().compareTo(BigDecimal.TEN) == -1) {
						return TransactionSize.SMALL;
					} else {
						return TransactionSize.BIG;
					}
				}, mapping(Transaction::getIssuer, toSet())));

		assertEquals(5, transactionSize2Issuer.get(TransactionSize.BIG).size());
		assertEquals(2, transactionSize2Issuer.get(TransactionSize.SMALL).size());
	}

	/**
	 * Group transactions to buy and not buy transactions.
	 */
	@Test
	public void testPartition() {
		List<Transaction> transactions = createTransactions();

		Map<Boolean, List<Transaction>> partitionedTransactions = transactions.stream()
				.collect(partitioningBy(Transaction::isBuy));

		assertEquals(2, partitionedTransactions.get(true).size());
		assertEquals(5, partitionedTransactions.get(false).size());
	}

	private List<Transaction> createTransactions() {
		List<Transaction> transactions = Arrays.asList(
				new Transaction("Andrzej", TransactionType.COST,
						Arrays.asList(new Chargeline(BigDecimal.TEN, "guma do żucia", 90),
								new Chargeline(BigDecimal.TEN, "cola", 21))),
				new Transaction("Romek", TransactionType.INCOME,
						Collections.singletonList(new Chargeline(BigDecimal.TEN, "cola", 21))),
				new Transaction("Mietek", TransactionType.INCOME,
						Collections.singletonList(new Chargeline(BigDecimal.TEN, "czipsy", 23))),
				new Transaction("Wiesiek", TransactionType.COST,
						Collections.singletonList(new Chargeline(BigDecimal.TEN, "żelki", 13))),
				new Transaction("Zbyszek", TransactionType.INCOME,
						Collections.singletonList(new Chargeline(BigDecimal.TEN, "ciastka", 16))),
				new Transaction("Tadeusz", TransactionType.INCOME,
						Collections.singletonList(new Chargeline(BigDecimal.ONE, "paluszki", 6))),
				new Transaction("Zbyszek", TransactionType.INCOME,
						Collections.singletonList(new Chargeline(BigDecimal.ONE, "sok", 7))));
		return transactions;
	}

}
