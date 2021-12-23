package cave.neuralnetwork;

import java.util.function.Function;

import cave.matrix.Matrix;

public class Approximator {

	public static Matrix gradient(Matrix input, Function<Matrix, Matrix> transform) {
		
		final double INC = 0.00000001;
		
		Matrix loss1 = transform.apply(input);
		
		assert loss1.getCols() == input.getCols(): "Input/loss columns not equal";
		assert loss1.getRows() == 1: "Transform does not return one single row";
		
		Matrix result = new Matrix(input.getRows(), input.getCols(), i->0);

		input.forEach((row, col, index, value)->{
			Matrix incremented = input.addIncrement(row, col, INC);
			
			Matrix loss2 = transform.apply(incremented);
			
			double rate = (loss2.get(col) - loss1.get(col))/INC;
			
			result.set(row, col, rate);
		});
		
		return result;
	}
	

	public static Matrix weightGradient(Matrix weights, Function<Matrix, Matrix> transform) {
		
		final double INC = 0.00000001;
		
		Matrix loss1 = transform.apply(weights);
		
		Matrix result = new Matrix(weights.getRows(), weights.getCols(), i->0);

		weights.forEach((row, col, index, value)->{
			Matrix incremented = weights.addIncrement(row, col, INC);
			
			Matrix loss2 = transform.apply(incremented);
			
			double rate = (loss2.get(0) - loss1.get(0))/INC;
			
			result.set(row, col, rate);
		});
		
		return result;
	}
}
