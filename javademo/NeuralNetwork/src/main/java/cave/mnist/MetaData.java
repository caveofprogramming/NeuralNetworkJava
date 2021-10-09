package cave.mnist;

public interface MetaData {
	int getNumberItems();

	void setNumberItems(int numberItems);

	int getInputSize();

	void setInputSize(int inputSize);

	int getExpectedSize();

	void setExpectedSize(int expectedSize);

	int getNumberBatches();

	void setNumberBatches(int numberBatches);

	int getTotalItemsRead();

	int getItemsRead();

	void setItemsRead(int itemsRead);

}
