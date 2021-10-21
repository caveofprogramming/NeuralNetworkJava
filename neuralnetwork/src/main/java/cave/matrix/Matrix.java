package cave.matrix;

public class Matrix {
	
	public interface Producer {
		double produce(int index);
	}
	
	private double[] a;
	
	public Matrix(int rows, int cols) {
		a = new double[rows * cols];
	}
	
	public Matrix(int rows, int cols, Producer producer) {
		a = new double[rows * cols];
		
		for(int i = 0; i < a.length; i++) {
			a[i] = producer.produce(i);
		}
	}
}
