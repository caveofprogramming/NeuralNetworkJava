package cave;

import cave.neuralnetwork.NeuralNetwork;
import cave.neuralnetwork.Transform;

public class App {

	public static void main(String[] args) {
		
		int inputRows = 10;
		int outputRows = 3;
		
		NeuralNetwork neuralNetwork = new NeuralNetwork();
		neuralNetwork.add(Transform.DENSE, 100, inputRows);
		neuralNetwork.add(Transform.RELU);
		neuralNetwork.add(Transform.DENSE, outputRows);
		neuralNetwork.add(Transform.SOFTMAX);
		
		
		System.out.println(neuralNetwork);
	}

}
