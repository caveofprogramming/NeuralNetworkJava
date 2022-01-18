package cave.neuralnetwork.loader.image;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import cave.neuralnetwork.loader.BatchData;
import cave.neuralnetwork.loader.Loader;

public class ImageLoader implements Loader {
	private String imageFileName;
	private String labelFileName;
	private int batchSize;

	private DataInputStream dsImages;
	private DataInputStream dsLabels;

	private ImageMetaData metaData;

	public ImageLoader(String imageFileName, String labelFileName, int batchSize) {
		this.imageFileName = imageFileName;
		this.labelFileName = labelFileName;
		this.batchSize = batchSize;
	}

	@Override
	public ImageMetaData open() {

		try {
			dsImages = new DataInputStream(new FileInputStream(imageFileName));
		} catch (Exception e) {
			throw new LoaderException("Cannot open " + imageFileName, e);
		}

		try {
			dsLabels = new DataInputStream(new FileInputStream(labelFileName));
		} catch (Exception e) {
			throw new LoaderException("Cannot open " + labelFileName, e);
		}

		metaData = readMetaData();
		return metaData;
	}

	private ImageMetaData readMetaData() {

		metaData = new ImageMetaData();

		int numberItems = 0;

		try {
			int magicLabelNumber = dsLabels.readInt();

			if (magicLabelNumber != 2049) {
				throw new LoaderException("Label file " + labelFileName + " has wrong format.");
			}

			numberItems = dsLabels.readInt();

			metaData.setNumberItems(numberItems);

		} catch (IOException e) {
			throw new LoaderException("Unable to read " + labelFileName, e);
		}

		try {
			int magicImageNumber = dsImages.readInt();

			if (magicImageNumber != 2051) {
				throw new LoaderException("Image file " + imageFileName + " has wrong format.");
			}

			if (dsImages.readInt() != numberItems) {
				throw new LoaderException(
						"Image file " + imageFileName + " has different number of items to " + labelFileName);
			}

			int height = dsImages.readInt();
			int width = dsImages.readInt();

			metaData.setHeight(height);
			metaData.setWidth(width);

			metaData.setInputSize(width * height);

			System.out.println(height + " , " + width);
		} catch (IOException e) {
			throw new LoaderException("Unable to read " + imageFileName, e);
		}

		metaData.setExpectedSize(10);
		metaData.setNumberBatches((int)Math.ceil((double) numberItems) / batchSize);

		return metaData;
	}

	@Override
	public void close() {

		metaData = null;

		try {
			dsImages.close();
		} catch (Exception e) {
			throw new LoaderException("Cannot close " + imageFileName, e);
		}

		try {
			dsLabels.close();
		} catch (Exception e) {
			throw new LoaderException("Cannot close " + labelFileName, e);
		}

	}

	@Override
	public ImageMetaData getMetaData() {
		return metaData;
	}

	@Override
	public BatchData readBatch() {
		// TODO Auto-generated method stub
		return null;
	}

}
