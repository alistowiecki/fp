package com.alistowi.fp;

import java.util.function.Supplier;

public class JVMLambdaReference {

	public Supplier<Integer> lambda = () -> 2;
	public Supplier<Integer> functionalInterface = new Supplier<Integer>() {
		@Override
		public Integer get() {
			return 2;
		}
	};

}
