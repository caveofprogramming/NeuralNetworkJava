package cave;

import cave.neuralnetwork.NeuralNetwork;
import cave.neuralnetwork.Transform;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.test.TestLoader;

public class App {

	public static void main(String[] args) {
		
		int inputRows = 10;
		int outputRows = 3;
		
		NeuralNetwork neuralNetwork = new NeuralNetwork();
		neuralNetwork.add(Transform.DENSE, 100, inputRows);
		neuralNetwork.add(Transform.RELU);
		neuralNetwork.add(Transform.DENSE, outputRows);
		neuralNetwork.add(Transform.SOFTMAX);
		
		neuralNetwork.setEpochs(20);
		neuralNetwork.setLearningRates(0.02, 0);
		
		Loader trainLoader = new TestLoader(60_000, 32);
		Loader testLoader = new TestLoader(10_000, 32);
		
		neuralNetwork.fit(trainLoader, testLoader);
		
		
		System.out.println(neuralNetwork);
	}

}
