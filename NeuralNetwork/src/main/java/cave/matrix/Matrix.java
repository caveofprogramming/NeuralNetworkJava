package cave.matrix;

public class Matrix {
	
	private int rows;
	private int cols;
	
	private final String NUMBER_FORMAT = "%5.2f ";
	
	public interface Producer {
		double produce(int row, int col, int index);
	}
	
	public interface IndexProducer {
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
		
		int index = 0;
		
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				a[index] = producer.produce(row, col, index);
				++index;
			}
		}
	}
	
	public Matrix(int rows, int cols, IndexProducer producer) {
		this(rows, cols, (row, col, index)->producer.produce(index));
	}
	
	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		int index = 0;
		
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				
				sb.append(String.format(NUMBER_FORMAT, a[index]));
				
				++index;
			}
			
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
