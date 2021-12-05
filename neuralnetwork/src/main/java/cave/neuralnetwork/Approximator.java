package cave.neuralnetwork;

import java.util.function.Function;

import cave.matrix.Matrix;

public class Approximator {

	public static Matrix gradient(Matrix input, Function<Matrix, Matrix> transform) {
		
		Matrix loss1 = transform.apply(input);
		
		assert loss1.getCols() == input.getCols(): "Input/loss columns not equal";
		assert loss1.getRows() == 1: "Transform does not return one single row";
		
		System.out.println(input);
		System.out.println(loss1);
		
		input.forEach((row, col, index, value)->{
			
		});
		
		return null;
	}
}
