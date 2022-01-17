package cave.neuralnetwork.loader.image;

import cave.neuralnetwork.loader.BatchData;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.MetaData;

public class ImageLoader implements Loader {
	private String imageFileName;
	private String labelFileName;
	private int batchSize;
	
	public ImageLoader(String imageFileName, String labelFileName, int batchSize) {
		this.imageFileName = imageFileName;
		this.labelFileName = labelFileName;
		this.batchSize = batchSize;
	}

	@Override
	public MetaData open() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchData readBatch() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
