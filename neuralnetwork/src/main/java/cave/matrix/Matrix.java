package cave.matrix;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Matrix implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String NUMBER_FORMAT = "%+12.5f";
	private double tolerance = 0.000001;

	private int rows;
	private int cols;

	public interface Producer {
		double produce(int index);
	}

	public interface IndexValueProducer {
		double produce(int index, double value);
	}

	public interface ValueProducer {
		double produce(double value);
	}

	public interface IndexValueConsumer {
		void consume(int index, double value);
	}
	
	public interface RowColValueConsumer {
		void consume(int row, int col, double value);
	}
	
	public interface RowColIndexValueConsumer {
		void consume(int row, int col, int index, double value);
	}

	public interface RowColProducer {
		double produce(int row, int col, double value);
	}

	private double[] a;

	public Matrix(int rows, int cols) {

		this.rows = rows;
		this.cols = cols;

		a = new double[rows * cols];
	}

	public Matrix(int rows, int cols, Producer producer) {
		this(rows, cols);

		for (int i = 0; i < a.length; i++) {
			a[i] = producer.produce(i);
		}
	}
	
	public Matrix(int rows, int cols, double[] values) {
		
		this.rows = rows;
		this.cols = cols;
		
		Matrix tmp = new Matrix(cols, rows);
		tmp.a = values;
		Matrix transposed = tmp.transpose();
		a = transposed.a;	
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}

	public Matrix apply(IndexValueProducer producer) {
		Matrix result = new Matrix(rows, cols);

		for (int i = 0; i < a.length; i++) {
			result.a[i] = producer.produce(i, a[i]);
		}

		return result;
	}

	public Matrix modify(RowColProducer producer) {

		int index = 0;

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {

				a[index] = producer.produce(row, col, a[index]);

				++index;
			}
		}

		return this;
	}

	public Matrix modify(ValueProducer producer) {

		for (int i = 0; i < a.length; ++i) {

			a[i] = producer.produce(a[i]);
		}

		return this;
	}
	
	public Matrix modify(IndexValueProducer producer) {

		for (int i = 0; i < a.length; ++i) {

			a[i] = producer.produce(i, a[i]);
		}

		return this;
	}
	
	public void forEach(RowColIndexValueConsumer consumer) {

		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				consumer.consume(row, col, index, a[index++]);
			}
		}
	}
	
	public void forEach(RowColValueConsumer consumer) {

		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				consumer.consume(row, col, a[index++]);
			}
		}
	}

	public void forEach(IndexValueConsumer consumer) {
		for (int i = 0; i < a.length; ++i) {
			consumer.consume(i, a[i]);
		}
	}

	public Matrix multiply(Matrix m) {
		Matrix result = new Matrix(rows, m.cols);

		assert cols == m.rows : "Cannot multiply; wrong number of rows vs cols";

		for (int row = 0; row < result.rows; row++) {
			for (int n = 0; n < cols; n++) {
				for (int col = 0; col < result.cols; col++) {
					result.a[row * result.cols + col] += a[row * cols + n] * m.a[col + n * m.cols];
				}
			}
		}

		return result;
	}
	
	public double sum() {
		double sum = 0;
		
		for(var v: a) {
			sum += v;
		}
		
		return sum;
	}
	
	public Matrix getGreatestRowNumbers() {
		Matrix result = new Matrix(1, cols);
		
		double[] greatest = new double[cols];
		
		for(int i = 0; i < cols; i++) {
			greatest[i] = Double.MIN_VALUE;
		}
		
		forEach((row, col, value)->{
			if(value > greatest[col]) {
				greatest[col] = value;
				result.a[col] = row;
			}
		});
		
		return result;
	}

	public Matrix sumColumns() {
		Matrix result = new Matrix(1, cols);
		
		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				result.a[col] += a[index++];
			}
		}

		return result;
	}
	
	public Matrix transpose() {
		Matrix result = new Matrix(cols, rows);
		
		for(int i = 0; i < a.length; ++i) {
			int row = i / cols;
			int col = i % cols;
			
			result.a[col * rows + row] = a[i];
		}
		
		return result;
	}
	
	public Matrix averageColumn() {
		Matrix result = new Matrix(rows, 1);
		
		forEach((row, col, index, value)->{
			result.a[row] += value/cols;
		});
		
		return result;
	}
	
	public Matrix softmax() {
		Matrix result = new Matrix(rows, cols, i->Math.exp(a[i]));
		
		Matrix colSum = result.sumColumns();
		
		result.modify((row, col, value)->{
			return value/colSum.get(col);
		});
		
		return result;
	}
	
	public void set(int row, int col, double value) {
		a[row * cols + col] = value;
	}
	
	public double get(int row, int col) {
		return a[row * cols + col];
	}
	
	public Matrix addIncrement(int row, int col, double increment) {
		
		Matrix result = apply((index, value)->a[index]);
		
		double originalValue = get(row, col);
		
		double newValue = originalValue + increment;
		
		result.set(row, col, newValue);
		
		return result;
	}

	public double get(int index) {
		return a[index];
	}
	
	

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(a);
		result = prime * result + Objects.hash(cols, rows);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matrix other = (Matrix) obj;

		for (int i = 0; i < a.length; i++) {
			if (Math.abs(a[i] - other.a[i]) > tolerance) {
				return false;
			}
		}

		return true;
	}
	
	public String toString(boolean showValues) {
		if(showValues) {
			return toString();
		}
		else {
			return rows + "x" + cols;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				sb.append(String.format(NUMBER_FORMAT, a[index]));

				index++;
			}

			sb.append("\n");
		}

		return sb.toString();
	}

	public double[] get() {
		return a;
	}
}
