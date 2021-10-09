package cave;

import static org.junit.Assert.*;

import org.junit.Test;

import cave.mnist.ImageLoader;
import cave.mnist.Loader;

public class TestLoadNetwork {

	@Test
	public void testLoad() {
		
		int batchSize = 32;

		var directory = "../data/MNIST/";

		var trainImageFile = String.format("%s%s", directory, "train-images-idx3-ubyte");
		var trainLabelFile = String.format("%s%s", directory, "train-labels-idx1-ubyte");
		var evalImageFile = String.format("%s%s", directory, "t10k-images-idx3-ubyte");
		var evalLabelFile = String.format("%s%s", directory, "t10k-labels-idx1-ubyte");

		Loader trainLoader = new ImageLoader(trainImageFile, trainLabelFile, batchSize);
		Loader evalLoader = new ImageLoader(evalImageFile, evalLabelFile, batchSize);

		NeuralNet neuralNet = new NeuralNet();

		neuralNet.load("save1.net");
		
		var start  = System.currentTimeMillis();
		neuralNet.fit(trainLoader, evalLoader);
		var end = System.currentTimeMillis();
		
		System.out.printf("Time taken: %ds\n", (end - start)/1000);
	}

}
