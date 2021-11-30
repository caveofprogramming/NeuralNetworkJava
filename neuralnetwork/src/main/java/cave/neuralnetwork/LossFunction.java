package cave.neuralnetwork;

import cave.matrix.Matrix;

public class LossFunction {
	public static double crossEntropy(Matrix expected, Matrix actual) {
		return actual.apply((index, value)->{
			return -expected.get(index) * Math.log(value);
		}).sum()/actual.getCols();
	}
}
