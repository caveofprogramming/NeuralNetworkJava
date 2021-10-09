package cave.mnist;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import cave.NeuralNet;

public class ImageWriter {

	private ImageMetaData metaData;
	private NeuralNet neuralNet;

	private int oneHotToInt(double[] oneHot) {

		int value = -1;
		double maxProb = 0;

		for (int k = 0; k < oneHot.length; k++) {
			if (oneHot[k] > maxProb) {
				value = k;
				maxProb = oneHot[k];
			}
		}

		return value;
	}

	public void writeMontage(BufferedImage montage, StringBuilder sb, double[] imagesData, double[] labelsData) {

		int imageSize = metaData.getInputSize();
		int labelSize = metaData.getExpectedSize();
		int numberImages = imagesData.length / imageSize;

		int imagesHorizontal = (int) Math.sqrt(numberImages);
		int imagesVertical = numberImages / imagesHorizontal;

		int imageIndex = 0;

		for (int imageRow = 0; imageRow < imagesVertical; imageRow++) {
			for (int imageCol = 0; imageCol < imagesHorizontal; imageCol++) {

				int imageStartIndex = imageIndex * imageSize;
				int imageEndIndex = imageStartIndex + imageSize;
				int labelStartIndex = imageIndex * labelSize;
				int labelEndIndex = labelStartIndex + labelSize;

				var imageData = Arrays.copyOfRange(imagesData, imageStartIndex, imageEndIndex);
				var labelData = Arrays.copyOfRange(labelsData, labelStartIndex, labelEndIndex);

				drawImage(montage, sb, imageRow, imageCol, imageData, labelData);

				imageIndex++;
			}
			
			sb.append("\n");
		}
	}

	private void drawImage(BufferedImage montage, StringBuilder sb, int imageRow, int imageCol, double[] imageData,
			double[] labelData) {

		int imageWidth = metaData.getWidth();
		int imageHeight = metaData.getHeight();
		int imageSize = metaData.getInputSize();

		int label = oneHotToInt(labelData);

		var predictionData = neuralNet.predict(imageData);
		var prediction = oneHotToInt(predictionData);

		int[] colorData = new int[imageSize];

		for (int i = 0; i < imageSize; i++) {
			int value = (int) (imageData[i] * 255);

			colorData[i] = (value << 16) + (value << 8) + value;
		}

		int pixelIndex = 0;

		if (prediction == label) {
			sb.append("  ");
		} else {
			for (int row = 0; row < imageHeight; row++) {
				for (int col = 0; col < imageWidth; col++) {
					int x = imageCol * imageWidth + col;
					int y = imageRow * imageHeight + row;

					montage.setRGB(x, y, colorData[pixelIndex++]);
				}
			}

			sb.append(String.format("%d ", prediction));
		}

	}

	public void run() {

		String directory = "../data/MNIST/";
		String imagesPath = String.format("%s%s", directory, "t10k-images-idx3-ubyte");
		String labelsPath = String.format("%s%s", directory, "t10k-labels-idx1-ubyte");

		ImageLoader loader = new ImageLoader(imagesPath, labelsPath, 900);

		metaData = loader.open();

		neuralNet = new NeuralNet();

		if (neuralNet.load("save2.net")) {
			System.out.println("Loaded network");
		} else {
			System.out.println("No saved neural net found");
			neuralNet = null;
		}

		for (int i = 0; i < metaData.getNumberBatches(); i++) {

			System.out.println("Writing batch " + i + " ...");

			var batchData = loader.readBatch();

			int numberImages = metaData.getItemsRead();

			int imageWidth = metaData.getWidth();
			int imageHeight = metaData.getHeight();

			int imagesHorizontal = (int) Math.sqrt(numberImages);
			int imagesVertical = numberImages / imagesHorizontal;

			int canvasWidth = imagesHorizontal * imageWidth;
			int canvasHeight = imagesVertical * imageHeight;

			var montage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_BYTE_GRAY);
			StringBuilder sb = new StringBuilder();

			var imagesData = batchData.getInputBatch();
			var labelsData = batchData.getExpectedBatch();

			String montagePath = String.format("%s/batch%d.jpg", "images", i);
			String textPath = String.format("%s/labels%d.txt", "images", i);

			writeMontage(montage, sb, imagesData, labelsData);

			try {
				ImageIO.write(montage, "jpg", new File(montagePath));
			} catch (IOException e) {
				System.out.println("Cannot write " + montagePath);
			}

			try (var fw = new FileWriter(textPath)) {
				fw.write(sb.toString());
			} catch (IOException e) {
				System.out.println("Unable to write " + textPath);
			}

		}

		System.out.println("Finished.");
	}

	public static void main(String[] args) {
		new ImageWriter().run();
	}

}
