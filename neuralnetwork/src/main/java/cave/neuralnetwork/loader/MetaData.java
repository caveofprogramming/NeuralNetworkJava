package cave.neuralnetwork.loader;

public interface MetaData {

	public int getNumberItems();

	public void setNumberItems(int numberItems);

	public int getInputSize();

	public void setInputSize(int inputSize);

	public int getExpectedSize();

	public void setExpectedSize(int expectedSize);

	public int getNumberBatches();

	public void setNumberBatches(int numberBatches);

	public int getTotalItemsRead();

	public void setTotalItemsRead(int totalItemsRead);

	public int getItemsRead();

	public void setItemsRead(int itemsRead);

}
