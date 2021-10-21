package cave.matrix;

public class Matrix {
	
	private static final String NUMBER_FORMAT = "%12.5f";
	
	private int rows;
	private int cols;
	
	public interface Producer {
		double produce(int index);
	}
	
	private double[] a;
	
	public Matrix(int rows, int cols) {
		
		this.rows = rows;
		this.cols = cols;
		
		a = new double[rows * cols];
	}
	
	public Matrix(int rows, int cols, Producer producer) {
		this(rows, cols);
		
		for(int i = 0; i < a.length; i++) {
			a[i] = producer.produce(i);
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		int index = 0;
		
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				sb.append(String.format(NUMBER_FORMAT, a[index]));
				
				index++;
			}
			
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
