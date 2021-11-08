package cave.matrix;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {

	private static final String NUMBER_FORMAT = "%+12.5f";
	private static final double TOLERANCE = 0.000001;

	private int rows;
	private int cols;

	public interface Producer {
		double produce(int index);
	}

	public interface ValueProducer {
		double produce(int index, double value);
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

	public Matrix apply(ValueProducer producer) {
		Matrix result = new Matrix(rows, cols);

		for (int i = 0; i < a.length; i++) {
			result.a[i] = producer.produce(i, a[i]);
		}

		return result;
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

	public double get(int index) {
		return a[index];
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
			if (Math.abs(a[i] - other.a[i]) > TOLERANCE) {
				return false;
			}
		}

		return true;
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
}
