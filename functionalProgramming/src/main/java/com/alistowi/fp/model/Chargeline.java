package com.alistowi.fp.model;

import java.math.BigDecimal;

public class Chargeline {

	private final BigDecimal amount;
	private final String description;
	private final int taxPercent;

	public Chargeline(BigDecimal amount, String description, int taxPercent) {
		this.amount = amount;
		this.description = description;
		this.taxPercent = taxPercent;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public int getTaxPercent() {
		return taxPercent;
	}

}
