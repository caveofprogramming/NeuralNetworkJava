package cave.neuralnetwork;

import java.util.Random;

import cave.matrix.Matrix;

public class Util {

	private static Random random = new Random();

	public static Matrix generateInputMatrix(int rows, int cols) {
		return new Matrix(rows, cols, i -> random.nextGaussian());
	}

	public static Matrix generateExpectedMatrix(int rows, int cols) {
		Matrix expected = new Matrix(rows, cols, i -> 0);

		for (int col = 0; col < cols; col++) {
			int randomRow = random.nextInt(rows);

			expected.set(randomRow, col, 1);
		}

		return expected;
	}

	public static Matrix generateTrainableExpectedMatrix(int outputRows, Matrix input) {
		Matrix expected = new Matrix(outputRows, input.getCols());
		
		Matrix columnSums = input.sumColumns();
		
		columnSums.forEach((row, col, value)->{
			int rowIndex = (int)(outputRows * (Math.sin(value) + 1.0)/2.0);
			
			expected.set(rowIndex, col, 1);
		});

		return expected;
	}
}
