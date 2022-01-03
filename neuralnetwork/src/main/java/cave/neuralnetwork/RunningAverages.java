package cave.neuralnetwork;

public class RunningAverages {
	
	private int nCalls = 0;
	private double[][] values;
	private Callback callback;
	
	public interface Callback {
		public void apply(int callNumber, double[] averages);
	}
	
	public RunningAverages(int numberAverages, int windowSize, Callback callback) {
		this.callback = callback;
		
		values = new double[numberAverages][windowSize];
		
		System.out.println(values.length);
		System.out.println(values[0].length);
	}
}
