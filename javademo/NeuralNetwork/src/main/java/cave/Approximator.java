package cave;

import java.util.function.Function;

public class Approximator {

	public static Matrix gradient(Matrix input, Function<Matrix, Matrix> transform) {
		
		double inc = 0.0000001;
		
		Matrix losses1 = transform.apply(input);
		
		assert losses1.getCols() == input.getCols(): "Approximator transform produces wrong number of columns";
		assert losses1.getRows() == 1: "Approximator transform produces wrong number of rows (" + losses1.getRows() + ")";
		
		Matrix result = new Matrix(input.getRows(), input.getCols());
		
		input.forEach((row, col, index, value)->{
			
			Matrix input2 = input.addToElement(row, col, inc);
			Matrix losses2 = transform.apply(input2);
			
			double loss1 = losses1.get(col);
			double loss2 = losses2.get(col);
			double rate = (loss2 - loss1)/inc;
			
			result.set(index, rate);
		});
		
		
		return result;
	}
}
