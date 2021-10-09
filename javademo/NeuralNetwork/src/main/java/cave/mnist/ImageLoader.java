package cave.mnist;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ImageLoader implements Loader {

	private String inputPath;
	private String expectedPath;

	private DataInputStream dsInput;
	private DataInputStream dsExpected;

	private ImageMetaData metaData;

	private int batchSize;

	private final Lock readLock = new ReentrantLock();

	public ImageLoader(String inputPath, String expectedPath, int batchSize) {

		this.inputPath = inputPath;
		this.expectedPath = expectedPath;
		this.batchSize = batchSize;
	}

	public ImageBatchData readBatch() {

		readLock.lock();

		try {

			ImageBatchData batchData = new ImageBatchData();

			int inputItemsRead = readInputBatch(batchData);
			int expectedItemsRead = readExpectedBatch(batchData);

			assert inputItemsRead == expectedItemsRead : "Number of input and expected items don't match";

			metaData.setItemsRead(inputItemsRead);

			return batchData;

		} finally {
			readLock.unlock();
		}
	}

	public ImageMetaData open() {

		try {
			dsInput = new DataInputStream(new FileInputStream(inputPath));
		} catch (FileNotFoundException e) {
			throw new LoadException("Cannot open " + inputPath);
		}

		try {
			dsExpected = new DataInputStream(new FileInputStream(expectedPath));
		} catch (FileNotFoundException e) {
			throw new LoadException("Cannot open " + expectedPath);
		}
		
		metaData = readMetaData();

		return metaData;
	}

	private ImageMetaData readMetaData() {

		metaData = new ImageMetaData();

		try {
			var magic = dsInput.readInt();

			if (magic != 0x803) {
				throw new LoadException("Not MNIST images in " + inputPath);
			}

			var itemsAvailable = dsInput.readInt();
			var rows = dsInput.readInt();
			var cols = dsInput.readInt();
			var imageSize = rows * cols;

			metaData.setNumberItems(itemsAvailable);
			metaData.setInputSize(imageSize);
			metaData.setNumberBatches((int) Math.ceil(itemsAvailable / batchSize));
			metaData.setHeight(rows);
			metaData.setWidth(cols);
		} catch (IOException e) {
			throw new LoadException("Error reading " + inputPath, e);
		}

		try {
			var magic = dsExpected.readInt();

			if (magic != 0x801) {
				throw new LoadException("Not MNIST labels in " + expectedPath);
			}

			var numberItems = dsExpected.readInt();

			assert numberItems == metaData
					.getNumberItems() : "Number of expected items not equal to number of input items";

			metaData.setExpectedSize(10);
		} catch (IOException e) {
			throw new LoadException("Error reading " + inputPath, e);
		}

		return metaData;
	}

	private int readInputBatch(ImageBatchData batchData) {

		try {
			var inputSize = metaData.getInputSize();
			var itemsRead = metaData.getTotalItemsRead();

			var numberToRead = Math.min(metaData.getNumberItems() - itemsRead, batchSize);
			
			int numberBytesToRead = numberToRead * inputSize;

			byte[] imageData = new byte[numberBytesToRead];

			dsInput.read(imageData, 0, numberBytesToRead);

			double[] data = new double[numberBytesToRead];

			for (int i = 0; i < numberBytesToRead; i++) {
					data[i] = (imageData[i] & 0xFF) / 255.0;
			}

			batchData.setInputBatch(data);

			return numberToRead;
		} catch (IOException e) {
			throw new LoadException("Cannot read " + inputPath, e);
		}
	}

	private int readExpectedBatch(ImageBatchData batchData) {

		try {
			var itemsRead = metaData.getTotalItemsRead();
			var numberToRead = Math.min(metaData.getNumberItems() - itemsRead, batchSize);

			var labelData = new byte[numberToRead];

			dsExpected.read(labelData, 0, numberToRead);

			double[] data = new double[numberToRead * 10];

			for (int i = 0; i < numberToRead; i++) {

				byte label = labelData[i];

				data[i *  10  + label] = 1;
			}

			batchData.setExpectedBatch(data);

			return numberToRead;
		} catch (IOException e) {
			throw new LoadException("Unable to load " + expectedPath, e);
		}
	}

	public void close() {

		try {
			dsInput.close();
		} catch (Exception e) {
			throw new LoadException("Unable to close " + inputPath, e);
		}

		try {
			dsExpected.close();
		} catch (Exception e) {
			throw new LoadException("Unable to close " + expectedPath, e);
		}

		metaData = null;
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Image loader: ").append("\n");
		sb.append(metaData).append("\n");
		
		return sb.toString();
	}

}
