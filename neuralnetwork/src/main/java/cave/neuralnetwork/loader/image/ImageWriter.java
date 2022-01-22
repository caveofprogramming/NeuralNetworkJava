package cave.neuralnetwork.loader.image;

import java.io.File;
import java.io.IOException;

import cave.neuralnetwork.loader.BatchData;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.MetaData;

public class ImageWriter {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("usage: [app] <MNIST DATA DIRECTORY>");
			return;
		}

		File dir = new File(args[0]);

		if (!dir.isDirectory()) {
			try {
				System.out.println(dir.getCanonicalPath() + " is not a directory.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		}

		String directory = args[0];
		
		new ImageWriter().run(directory);
	}
	
	public void run(String directory) {
		final String trainImages = String.format("%s%s%s", directory, File.separator, "train-images-idx3-ubyte");
		final String trainLabels = String.format("%s%s%s", directory, File.separator, "train-labels-idx1-ubyte");
		final String testImages = String.format("%s%s%s", directory, File.separator, "t10k-images-idx3-ubyte");
		final String testLabels = String.format("%s%s%s", directory, File.separator, "t10k-labels-idx1-ubyte");

		ImageLoader trainLoader = new ImageLoader(trainImages, trainLabels, 32);
		ImageLoader testLoader = new ImageLoader(testImages, testLabels, 32);
		
		ImageLoader loader = testLoader;

		ImageMetaData metaData = loader.open();
		
		for (int i = 0; i < metaData.getNumberBatches(); i++) {
			BatchData batchData = testLoader.readBatch();
		}

		loader.close();
	}
}
