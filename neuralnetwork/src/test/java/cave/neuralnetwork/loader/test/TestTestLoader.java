package cave.neuralnetwork.loader.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cave.neuralnetwork.loader.BatchData;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.MetaData;

public class TestTestLoader {

	@Test
	void test() {
		
		int batchSize = 32;
		
		Loader testLoader = new TestLoader(60_000, batchSize);
		
		MetaData metaData = testLoader.open();
		
		for(int i = 0; i < metaData.getNumberBatches(); i++) {
			BatchData batchData = testLoader.readBatch();
			
			assertTrue(batchData != null);
			
			int itemsRead = metaData.getItemsRead();
			
			assertTrue(itemsRead == batchSize);
		}
	}

}
