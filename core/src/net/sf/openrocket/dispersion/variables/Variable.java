package net.sf.openrocket.dispersion.variables;

import java.util.Random;

/*
Adapted from Bill Kuker's Dispersion project by Chris Andre
 */

public abstract class Variable extends Number {
	private static final long serialVersionUID = 1L;
	private static final Random r = new Random(0);

	protected double randomDouble() {
		return r.nextDouble();
	}

	protected double randomGaussian() {
		return r.nextDouble();
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public int intValue() {
		return (int) doubleValue();
	}

	@Override
	public long longValue() {
		return (long) doubleValue();
	}
}
