package cave;

import cave.neuralnetwork.NeuralNetwork;
import cave.neuralnetwork.Transform;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.test.TestLoader;

public class GeneratedDataApp {

	public static void main(String[] args) {
		
		String filename = "neural1.net";
		
		//System.out.println(Runtime.getRuntime().availableProcessors());
		
		NeuralNetwork neuralNetwork = NeuralNetwork.load(filename);
		
		if(neuralNetwork == null) {
			System.out.println("Unable to load neural network from saved. Creating from scratch.");
			
			int inputRows = 10;
			int outputRows = 3;
			
			neuralNetwork = new NeuralNetwork();
			neuralNetwork.add(Transform.DENSE, 100, inputRows);
			neuralNetwork.add(Transform.RELU);
			neuralNetwork.add(Transform.DENSE, 50);
			neuralNetwork.add(Transform.RELU);
			neuralNetwork.add(Transform.DENSE, outputRows);
			neuralNetwork.add(Transform.SOFTMAX);
			
			neuralNetwork.setThreads(4);
			neuralNetwork.setEpochs(100);
			neuralNetwork.setLearningRates(0.02, 0.001);
		}
		else {
			System.out.println("Loaded from " + filename);
		}
		
		
		System.out.println(neuralNetwork);
		
		Loader trainLoader = new TestLoader(60_000, 32);
		Loader testLoader = new TestLoader(10_000, 32);
		
		neuralNetwork.fit(trainLoader, testLoader);
		
		if(neuralNetwork.save(filename)) {
			System.out.println("Saved to " + filename);
		}
		else {
			System.out.println("Unable to save to " + filename);
		}
		
	}

}
