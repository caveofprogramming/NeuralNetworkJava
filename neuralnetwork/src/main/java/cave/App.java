package cave;

import java.io.File;

import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.image.ImageLoader;

public class App {

	public static void main(String[] args) {
		
		if(args.length == 0 && new File(args[0]).isDirectory()) {
			System.out.println("usage: [app] <MNIST DATA DIRECTORY>");
			return;
		}
		
		final String trainImages = "train-images-idx3-ubyte";
		final String trainLabels = "train-labels-idx1-ubyte";
		final String testImages = "t10k-images-idx3-ubyte";
		final String testLabels = "t10k-labels-idx1-ubyte";
	
		Loader trainLoader = new ImageLoader(trainImages, trainLabels, 32);
		Loader testLoader = new ImageLoader(testImages, testLabels, 32);
	}

}
