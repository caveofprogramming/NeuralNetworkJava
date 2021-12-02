package cave.neuralnetwork;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import cave.matrix.Matrix;

class NeuralNetTest {
	private Random random = new Random();
	
	@Test
	void testCrossEntropy() {
		double[] expectedValues = {1, 0, 0, 0, 0, 1, 0, 1, 0};
		Matrix expected = new Matrix(3, 3, i->expectedValues[i]);
		
		Matrix actual = new Matrix(3, 3, i->0.05 * i*i).softmax();
		
		Matrix result = LossFunction.crossEntropy(expected, actual);
		
		actual.forEach((row, col, index, value)->{
			double expectedValue = expected.get(index);
			
			double loss = result.get(col);
			
			if(expectedValue > 0.9) {
				assertTrue(Math.abs(Math.log(value) + loss) < 0.001);
			}
		});
	}
	
	//@Test
	void testEngine() {
		Engine engine = new Engine();
		
		engine.add(Transform.DENSE, 8, 5);
		engine.add(Transform.RELU);
		engine.add(Transform.DENSE, 5);
		engine.add(Transform.RELU);
		engine.add(Transform.DENSE, 4);
		engine.add(Transform.SOFTMAX);
		
		Matrix input = new Matrix(5, 4, i->random.nextGaussian());
		
		Matrix output = engine.runForwards(input);
		
		System.out.println(engine);
		System.out.println(output);
	}
	
	//@Test
	void testTemp() {
		
		int inputSize = 5;
		int layer1Size = 6;
		int layer2Size = 4;
		
		Matrix input = new Matrix(inputSize, 1, i->random.nextGaussian());

		Matrix layer1Weights = new Matrix(layer1Size, input.getRows(), i->random.nextGaussian());
		Matrix layer1biases = new Matrix(layer1Size, 1, i->random.nextGaussian());
		
		Matrix layer2Weights = new Matrix(layer2Size, layer1Weights.getRows(), i->random.nextGaussian());
		Matrix layer2biases = new Matrix(layer2Size, 1, i->random.nextGaussian());
		
		var output = input;
		System.out.println(output);
		
		output = layer1Weights.multiply(output);
		System.out.println(output);
		
		output = output.modify((row, col, value) -> value + layer1biases.get(row));
		System.out.println(output);
		
		output = output.modify(value -> value > 0 ? value: 0);
		System.out.println(output);
		
		output = layer2Weights.multiply(output);
		System.out.println(output);
		
		output = output.modify((row, col, value) -> value + layer2biases.get(row));
		System.out.println(output);
		
		output = output.softmax();
		System.out.println(output);
		
	}

	@Test
	void testAddBias() {

		Matrix input = new Matrix(3, 3, i -> (i + 1));
		Matrix weights = new Matrix(3, 3, i -> (i + 1));
		Matrix biases = new Matrix(3, 1, i -> (i + 1));

		Matrix result = weights.multiply(input).modify((row, col, value) -> value + biases.get(row));

		double[] expectedValues = { +31.00000, +37.00000, +43.00000, +68.00000, +83.00000, +98.00000, +105.00000,
				+129.00000, +153.00000 };
		
		Matrix expected = new Matrix(3, 3, i->expectedValues[i]);
		
		assertTrue(expected.equals(result));
	}
	
	@Test
	void testReLu() {
		
		final int numberNeurons = 5;
		final int numberInputs = 6;
		final int inputSize = 4;

		Matrix input = new Matrix(inputSize, numberInputs, i -> random.nextDouble());
		Matrix weights = new Matrix(numberNeurons, inputSize, i -> random.nextGaussian());
		Matrix biases = new Matrix(numberNeurons, 1, i -> random.nextGaussian());

		Matrix result1 = weights.multiply(input).modify((row, col, value) -> value + biases.get(row));
		Matrix result2 = weights.multiply(input).modify((row, col, value) -> value + biases.get(row)).modify(value -> value > 0 ? value: 0);
		
		result2.forEach((index, value)->{
			double originalValue = result1.get(index);
			
			if(originalValue > 0) {
				assertTrue(Math.abs(originalValue - value) < 0.000001);
			}
			else {
				assertTrue(Math.abs(value) < 0.000001);
			}
		});

	}

}
