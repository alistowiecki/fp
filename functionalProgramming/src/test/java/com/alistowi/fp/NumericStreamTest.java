package com.alistowi.fp;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.junit.Test;

import com.alistowi.fp.model.Chargeline;

public class NumericStreamTest {

	/**
	 * Calculate average tax for chargelines.
	 */
	@Test
	public void boxingTest() {
		List<Chargeline> chargelines = getChargelines(40000);
		
		long time = measureTime(() -> chargelines.stream().map(Chargeline::getTaxPercent).reduce((d1, d2) -> d1 + d2).get()
				/ chargelines.size());
		System.out.println("Stream " + time);
		
		time = measureTime(() -> chargelines.stream().mapToInt(Chargeline::getTaxPercent).average());
		System.out.println("Stream int " + time);
		
		time = measureTime(() -> chargelines.parallelStream().mapToInt(Chargeline::getTaxPercent).average());
		System.out.println("Paralell stream int " + time);
		
		time = measureTime(() -> {
			int sum = 0;
			for (Chargeline chargeline : chargelines) {
				sum += chargeline.getTaxPercent();
			}
			return sum / chargelines.size();
		});
		System.out.println("For " + time);

	}

	private List<Chargeline> getChargelines(int numberOfChargelines) {
		return new Random().ints(0, 100).limit(numberOfChargelines).mapToObj((i) -> {
			return new Chargeline(BigDecimal.ONE, "description", i);
		}).collect(toList());
	}

	private <T> long measureTime(Supplier<T> supplier) {
		long start = System.nanoTime();
		for (int i = 0; i < 250000; i++) {
			supplier.get();
		}
		return (System.nanoTime() - start) / 250000;
	}

}
