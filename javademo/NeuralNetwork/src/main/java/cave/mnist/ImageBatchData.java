package cave.mnist;

public class ImageBatchData implements BatchData {
	
	private double[] inputBatch;
	private double[] expectedBatch;
	
	public double[] getInputBatch() {
		return inputBatch;
	}

	public void setInputBatch(double[] inputBatch) {
		this.inputBatch = inputBatch;
	}

	public double[] getExpectedBatch() {
		return expectedBatch;
	}

	public void setExpectedBatch(double[] expectedBatch) {
		this.expectedBatch = expectedBatch;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(inputBatch).append("\n");
		sb.append(expectedBatch).append("\n");
		
		return sb.toString();
	}
}
