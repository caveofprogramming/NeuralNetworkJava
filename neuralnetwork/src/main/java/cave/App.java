package cave;

import java.io.File;
import java.io.IOException;

import cave.neuralnetwork.NeuralNetwork;
import cave.neuralnetwork.Transform;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.MetaData;
import cave.neuralnetwork.loader.image.ImageLoader;

public class App {

	public static void main(String[] args) {
		
		final String filename = "mnistNeural0.net";
		
		if(args.length == 0) {
			System.out.println("usage: [app] <MNIST DATA DIRECTORY>");
			return;
		}
		
		File dir = new File(args[0]);
		
		if(!dir.isDirectory()) {
			try {
				System.out.println(dir.getCanonicalPath() + " is not a directory.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}
		
		String directory = args[0];
		
		final String trainImages = String.format("%s%s%s", directory, File.separator,"train-images-idx3-ubyte");
		final String trainLabels = String.format("%s%s%s", directory, File.separator,"train-labels-idx1-ubyte");
		final String testImages = String.format("%s%s%s", directory, File.separator,"t10k-images-idx3-ubyte");
		final String testLabels = String.format("%s%s%s", directory, File.separator,"t10k-labels-idx1-ubyte");
	
		Loader trainLoader = new ImageLoader(trainImages, trainLabels, 32);
		Loader testLoader = new ImageLoader(testImages, testLabels, 32);
		
		MetaData metaData = trainLoader.open();
		int inputSize = metaData.getInputSize();
		int outputSize = metaData.getExpectedSize();
		trainLoader.close();
		
		NeuralNetwork neuralNetwork = NeuralNetwork.load(filename);
		
		if(neuralNetwork == null) {
			System.out.println("Unable to load neural network from saved. Creating from scratch.");
			
			neuralNetwork = new NeuralNetwork();
			neuralNetwork.setScaleInitialWeights(0.2);
			neuralNetwork.setThreads(4);
			neuralNetwork.setEpochs(100);
			neuralNetwork.setLearningRates(0.02, 0.001);
			
			neuralNetwork.add(Transform.DENSE, 200, inputSize);
			neuralNetwork.add(Transform.RELU);
			neuralNetwork.add(Transform.DENSE, outputSize);
			neuralNetwork.add(Transform.SOFTMAX);
			
		}
		else {
			System.out.println("Loaded from " + filename);
		}
		
		System.out.println(neuralNetwork);

		neuralNetwork.fit(trainLoader, testLoader);
		
		if(neuralNetwork.save(filename)) {
			System.out.println("Saved to " + filename);
		}
		else {
			System.out.println("Unable to save to " + filename);
		}
	}

}
