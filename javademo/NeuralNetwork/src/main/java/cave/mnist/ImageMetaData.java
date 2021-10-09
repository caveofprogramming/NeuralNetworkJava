package cave.mnist;

public class ImageMetaData implements MetaData {
	
	private int numberItems;
	private int inputSize;
	private int expectedSize;
	private int numberBatches;
	private int totalItemsRead;
	private int itemsRead;
	private int width;
	private int height;

	@Override
	public int getNumberItems() {
		return numberItems;
	}

	@Override
	public void setNumberItems(int numberItems) {
		this.numberItems = numberItems;
	}

	@Override
	public int getInputSize() {
		return inputSize;
	}

	@Override
	public void setInputSize(int inputSize) {
		this.inputSize = inputSize;
	}

	@Override
	public int getExpectedSize() {
		return this.expectedSize;
	}

	@Override
	public void setExpectedSize(int expectedSize) {
		this.expectedSize = expectedSize;
	}

	@Override
	public int getNumberBatches() {
		return numberBatches;
	}

	@Override
	public void setNumberBatches(int numberBatches) {
		this.numberBatches = numberBatches;
		
	}
	
	public int getTotalItemsRead() {
		return totalItemsRead;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("numberInputs: ").append(numberItems).append("\n");
		sb.append("inputSize: ").append(inputSize).append("\n");
		sb.append("expectedSize: ").append(expectedSize).append("\n");
		sb.append("numberBatches: ").append(numberBatches).append("\n");
		sb.append("itemsRead: ").append(totalItemsRead).append("\n");
		
		return sb.toString();
	}
	
	public int getItemsRead() {
		return itemsRead;
	}

	public void setItemsRead(int itemsRead) {
		this.itemsRead = itemsRead;
		this.totalItemsRead += itemsRead;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	
}
