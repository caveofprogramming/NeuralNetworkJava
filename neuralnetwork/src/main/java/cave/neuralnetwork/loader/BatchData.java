package cave.neuralnetwork.loader;

public interface BatchData {

	public double[] getInputBatch();

	public void setInputBatch(double[] inputBatch);

	public double[] getExpectedBatch();

	public void setExpectedBatch(double[] expectedBatch);

}
