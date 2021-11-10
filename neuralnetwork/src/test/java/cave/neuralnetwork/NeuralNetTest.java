package cave.neuralnetwork;

import org.junit.jupiter.api.Test;

import cave.matrix.Matrix;

class NeuralNetTest {

	@Test
	void test() {
		
		Matrix input = new Matrix(3, 1, i->(i+1));
		Matrix weights = new Matrix(3, 3, i->(i+1));
		Matrix biases = new Matrix(3, 1, i->(i+1));
		
		Matrix result = weights.multiply(input);
		
		System.out.println(input);
		System.out.println(weights);
		System.out.println(biases);
		System.out.println(result);
	}

}
