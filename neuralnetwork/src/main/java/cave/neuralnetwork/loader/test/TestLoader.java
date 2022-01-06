package cave.neuralnetwork.loader.test;

import cave.neuralnetwork.loader.BatchData;
import cave.neuralnetwork.loader.MetaData;

public class TestLoader implements cave.neuralnetwork.loader.Loader {
	
	private MetaData metaData;
	
	private int numberItems = 60_000;
	private int inputSize = 500;
	private int expectedSize = 3;
	private int numberBatches;
	private int batchSize = 32;
	
	private int totalItemsRead;
	private int itemsRead;

	public TestLoader() {
		metaData = new TestMetaData();
		metaData.setNumberItems(numberItems);
		
		numberBatches = numberItems/batchSize;
		
		if(numberItems % batchSize != 0) {
			numberBatches += 1;
		}
		
		metaData.setNumberBatches(numberBatches);
		metaData.setInputSize(inputSize);
		metaData.setExpectedSize(expectedSize);
	}

	@Override
	public MetaData open() {
		return metaData;
	}

	@Override
	public void close() {
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public BatchData readBatch() {
		// TODO Auto-generated method stub
		return null;
	}

}
