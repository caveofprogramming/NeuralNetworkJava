package cave;

import java.util.Random;

public class Util {

	private static Random random = new Random();

	public static Matrix arrayToMatrix(double[][] array) {

		return new Matrix(array[0].length, array.length, (row, col, index, value) -> array[col][row]);
	}
	
	public static double[][] generateInput(int vectorLength, int numberItems) {
		double[][] values = new double[numberItems][vectorLength];
		
		for(int i = 0; i < numberItems; i++) {
			for(int k = 0; k < vectorLength; k++) {
				values[i][k] = 2.0 * random.nextDouble() - 1.0;
			}
		}
		
		return values;
	}
	
	public static Matrix generateInputMatrix(int rows, int cols) {
		return arrayToMatrix(generateInput(rows, cols));
	}
	
	public static Matrix generateExpectedMatrix(int rows, int cols) {
		return arrayToMatrix(generateOneHotExpected(rows, cols));
	}

	public static double[][] generateOneHotExpected(int vectorLength, int numberItems) {

		double[][] values = new double[numberItems][vectorLength];

		for (int i = 0; i < numberItems; i++) {
			int value = random.nextInt(vectorLength);

			values[i][value] = 1.0;
		}

		return values;
	}
}
