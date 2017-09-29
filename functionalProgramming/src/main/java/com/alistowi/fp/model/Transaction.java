package com.alistowi.fp.model;

import java.math.BigDecimal;
import java.util.List;

public class Transaction {

	private final String issuer;
	private final TransactionType type;
	private final BigDecimal totalAmount;
	private List<Chargeline> chargelines;

	public Transaction(String issuer, TransactionType type, List<Chargeline> chargelines) {
		this.chargelines = chargelines;
		this.issuer = issuer;
		this.type = type;
		this.totalAmount = chargelines.stream().map(Chargeline::getAmount).reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO);
	}
	
	public String getIssuer() {
		return issuer;
	}

	public TransactionType getType() {
		return type;
	}

	public boolean isBuy() {
		return type == TransactionType.COST;
	}
	
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	
	public List<Chargeline> getChargelines() {
		return chargelines;
	}

}
