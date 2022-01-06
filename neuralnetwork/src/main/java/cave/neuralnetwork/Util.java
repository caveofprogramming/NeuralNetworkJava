package cave.neuralnetwork;

import java.util.Random;

import cave.matrix.Matrix;

public class Util {

	private static Random random = new Random();

	public static Matrix generateInputMatrix(int rows, int cols) {
		return new Matrix(rows, cols, i -> random.nextGaussian());
	}
	

	public static TrainingArrays generateTrainingArrays(int inputSize, int outputSize, int numberItems) {
		
		double[] input = new double[inputSize * numberItems];
		double[] output = new double[outputSize * numberItems];
		
		int inputPos = 0;
		int outputPos = 0;
		
		for(int col = 0; col < numberItems; col++) {
			int radius = random.nextInt(outputSize);
			
			double[] values = new double[inputSize];
			
			double initialRadius = 0;
			
			for(int row = 0; row < inputSize; row++) {
				double value = random.nextGaussian();
				values[row] = value;
				initialRadius += value * value;
			}
			
			initialRadius = Math.sqrt(initialRadius);
			
			for(int row = 0; row < inputSize; row++) {
				input[inputPos++] = values[row] * radius/initialRadius;
			}
			
			output[outputPos + radius] = 1;
			
			outputPos += outputSize;
		}
		
		return new TrainingArrays(input, output);
	}
	
	public static TrainingMatrixes generateTrainingMatrixes(int inputRows, int outputRows, int cols) {
		
		var io = generateTrainingArrays(inputRows, outputRows, cols);
		
		Matrix input = new Matrix(inputRows, cols, io.getInput());
		Matrix output = new Matrix(outputRows, cols, io.getOutput());
		
		return new TrainingMatrixes(input, output);
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
