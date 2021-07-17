package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

public class SawtoothFunction implements ActivationFunction {

	/**
	 * Sawtooth function for x. mimics sine but in piecewise way. Uses
	 * Math.floor().
	 *
	 * @param x Function parameter
	 * @return value of sawtooth(x)
	 */
	@Override
	public double f(double x) {
		return x - Math.floor(x);
	}

	@Override
	public Activation equivalentDL4JFunction() {
		throw new UnsupportedOperationException("No corresponding DL4J function for " + name());
	}

	@Override
	public String name() {
		return "sawtooth";
	}
}
