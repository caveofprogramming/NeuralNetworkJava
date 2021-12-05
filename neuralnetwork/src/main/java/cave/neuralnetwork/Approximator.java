package cave.neuralnetwork;

import java.util.function.Function;

import cave.matrix.Matrix;

public class Approximator {

	public static Matrix gradient(Matrix input, Function<Matrix, Matrix> transform) {
		
		Matrix loss1 = transform.apply(input);
		
		assert loss1.getCols() == input.getCols(): "Input/loss columns not equal";
		assert loss1.getRows() == 1: "Transform does not return one single row";
		
		input.forEach((row, col, index, value)->{
			System.out.printf("%12.5f", value);
			
			if(col == input.getCols() - 1) {
				System.out.println();
			}
		});
		
		return null;
	}
}
