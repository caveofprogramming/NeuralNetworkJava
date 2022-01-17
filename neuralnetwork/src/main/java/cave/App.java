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
		
		String directory = args[0];
		
		final String trainImages = String.format("%s%s%s", directory, File.separator,"train-images-idx3-ubyte");
		final String trainLabels = String.format("%s%s%s", directory, File.separator,"train-labels-idx1-ubyte");
		final String testImages = String.format("%s%s%s", directory, File.separator,"t10k-images-idx3-ubyte");
		final String testLabels = String.format("%s%s%s", directory, File.separator,"t10k-labels-idx1-ubyte");
	
		Loader trainLoader = new ImageLoader(trainImages, trainLabels, 32);
		Loader testLoader = new ImageLoader(testImages, testLabels, 32);
		
		trainLoader.open();
		testLoader.open();
		
		trainLoader.close();
		testLoader.close();
	}

}
