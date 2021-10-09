package cave;

import cave.mnist.ImageLoader;
import cave.mnist.Loader;

public class App {

	public static void main(String[] args) {

		int batchSize = 32;

		var directory = "../data/MNIST/";

		var trainImageFile = String.format("%s%s", directory, "train-images-idx3-ubyte");
		var trainLabelFile = String.format("%s%s", directory, "train-labels-idx1-ubyte");
		var evalImageFile = String.format("%s%s", directory, "t10k-images-idx3-ubyte");
		var evalLabelFile = String.format("%s%s", directory, "t10k-labels-idx1-ubyte");

		Loader trainLoader = new ImageLoader(trainImageFile, trainLabelFile, batchSize);
		Loader evalLoader = new ImageLoader(evalImageFile, evalLabelFile, batchSize);

		NeuralNet neuralNet = new NeuralNet();

		if (neuralNet.load("save1.net")) {
			System.out.println("Loaded network");
			System.out.println(neuralNet.toString());
			neuralNet.setEpochs(100);
		} else {
			int inputSize = 784;
			int hiddenLayer1 = 256;
			int hiddenLayer2 = 128;
			int outputSize = 10;

			neuralNet.setEpochs(100);

			neuralNet.add(Transform.WEIGHTEDSUM, hiddenLayer1, inputSize);
			neuralNet.add(Transform.RELU);
			//neuralNet.add(Transform.DROPOUT);
			//neuralNet.add(Transform.WEIGHTEDSUM, hiddenLayer2, hiddenLayer1);
			//neuralNet.add(Transform.RELU);
			//neuralNet.add(Transform.DROPOUT);
			neuralNet.add(Transform.WEIGHTEDSUM, outputSize, hiddenLayer1);
			neuralNet.add(Transform.SOFTMAX);
		}

		var start = System.currentTimeMillis();
		neuralNet.fit(trainLoader, evalLoader);
		var end = System.currentTimeMillis();

		if (neuralNet.save("save1.net")) {
			System.out.println("Saved network state");
		}

		System.out.printf("Time taken: %ds\n", (end - start) / 1000);
	}

}
