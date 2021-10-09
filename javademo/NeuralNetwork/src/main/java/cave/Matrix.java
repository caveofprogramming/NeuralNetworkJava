package cave;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class Matrix implements Serializable {
	private static final long serialVersionUID = 1L;
	private int rows;
	private int cols;

	private double tolerance = 0.0000001;

	private double[] a;

	private final static String NUMBER_FORMAT = "%+11.6f ";
	private final static int MAX_PRINT = 12;

	public interface Consumer {
		void consume(int row, int col, int index, double value);
	}

	public interface Producer {
		double produce(int row, int col, int index, double value);
	}

	public interface RowColProducer {
		double produce(int row, int col);
	}

	public interface IndexProducer {
		double produce(int index);
	}
	
	public interface ValueProducer {
		double produce(double value);
	}

	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		a = new double[rows * cols];
	}

	public Matrix(int rows, int cols, boolean itemsByRow, double[] values) {

		this.rows = rows;
		this.cols = cols;

		if (itemsByRow) {
			this.a = values;
		} else {
			Matrix m = new Matrix(cols, rows);
			m.a = values;
			Matrix transposed = m.transpose();
			a = transposed.a;
		}
	}

	public Matrix(int rows, int cols, Producer producer) {
		this(rows, cols);

		modify(producer);
	}

	public Matrix(int rows, int cols, RowColProducer producer) {
		this(rows, cols);

		modify((row, col, index, value) -> producer.produce(row, col));
	}

	public Matrix(int rows, int cols, IndexProducer producer) {
		this(rows, cols);

		modify((row, col, index, value) -> producer.produce(index));
	}

	/**************************************************************************
	 * 
	 * Arithmetic
	 */

	public Matrix addToAllColumns(Matrix column, boolean bModify) {

		Producer p = (row, col, index, value) -> {
			return a[index] + column.a[row];
		};

		if (bModify) {
			modify(p);
			return this;
		}

		return new Matrix(rows, cols, p);
	}

	public Matrix add(Matrix other) {

		assert rows == other.rows && cols == other.cols : "Matrix sizes don't match";

		return new Matrix(rows, cols, (rows, cols, index, value) -> {
			return a[index] + other.a[index];
		});
	}

	public Matrix subtract(Matrix other) {

		assert rows == other.rows && cols == other.cols : "Matrix sizes don't match (" + toString(false) + ", "
				+ other.toString(false);

		return new Matrix(rows, cols, (rows, cols, index, value) -> {
			return a[index] - other.a[index];
		});
	}

	public void subtractFraction(Matrix other, double factor) {

		double size = rows * cols;

		for (int i = 0; i < size; i++) {
			a[i] = a[i] - factor * other.a[i];
		}
	}

	public int countEntries() {
		return rows * cols;
	}

	public Matrix multiply(Matrix other) {

		assert cols == other.rows : "Cannot multiply matrixes " + toString(false) + ", " + other.toString(false);

		int resultRows = rows;
		int resultCols = other.cols;
		int mid = cols;

		Matrix result = new Matrix(resultRows, resultCols);

		for (int n = 0; n < mid; n++) {

			for (int row = 0; row < resultRows; row++) {
				for (int col = 0; col < resultCols; col++) {
					result.a[row * resultCols + col] += a[row * cols + n] * other.a[col + n * other.cols];
				}
			}
		}

		return result;
	}

	public Matrix experimentalMultiply(Matrix other) {

		assert cols == other.rows : "Cannot multiply matrixes " + toString(false) + ", " + other.toString(false);

		int resultRows = rows;
		int resultCols = other.cols;
		int mid = cols;

		Matrix result = new Matrix(resultRows, resultCols);

		var size = resultCols * resultRows;

		int row = 0;
		int col = 0;

		for (int i = 0; i < size; i++) {
			row = i / resultCols;
			col = i % resultCols;

			for (int n = 0; n < mid; n++) {

				result.a[i] += a[row * cols + n] * other.a[col + n * other.cols];
			}
		}

		return result;

	}

	public Matrix multiplyMod(double d) {
		for (int i = 0; i < a.length; i++) {
			a[i] = a[i] * d;
		}

		return this;
	}

	Matrix addToElement(int row, int col, double d) {

		return apply((thisRow, thisCol, index, value) -> {

			if (thisRow == row && thisCol == col) {
				return value + d;
			}

			return value;
		});
	}

	/**************************************************************************
	 * 
	 * Other mathematical functions
	 */
	
	
	

	public Matrix transpose() {

		Matrix result = new Matrix(cols, rows);

		int size = rows * cols;

		for (int i = 0; i < size; i++) {

			int row = i / cols;
			int col = i % cols;

			result.a[col * rows + row] = a[i];
		}

		return result;
	}


	public Matrix slowTranspose() {
		return new Matrix(cols, rows, (col, row, index, value) -> a[row * cols + col]);
	}

	public Matrix softmax() {

		Matrix result = apply((row, col, index, value) -> Math.exp(value));
		Matrix columnSums = result.sumColumns();

		result.modify((row, col, index, value) -> {
			return value / columnSums.a[col];
		});

		return result;
	}

	public Matrix reLu() {
		return apply((row, col, index, value) -> {
			return value > 0 ? value : 0;
		});
	}

	/**************************************************************************
	 * 
	 * Reduction
	 */

	public Matrix maxValuesByColumn() {
		double[] values = new double[cols];

		forEach((row, col, index, value) -> {
			if (value > values[col]) {
				values[col] = value;
			}
		});

		return new Matrix(1, cols, (row, col, index, value) -> {
			return values[index];
		});

	}

	public Matrix averageRows() {

		double[] averages = new double[rows];

		forEach((row, col, index, value) -> {
			averages[row] += value;
		});

		return new Matrix(1, cols, (row, col, index, value) -> {
			return averages[row] / cols;
		});
	}

	public Matrix averageColumns() {

		double[] averages = new double[rows];

		forEach((row, col, index, value) -> {
			averages[row] += value;
		});

		return new Matrix(rows, 1, (row, col, index, value) -> {
			return averages[row] / cols;
		});
	}

	public Matrix sumRows() {
		double[] values = new double[rows];

		forEach((row, col, index, value) -> {
			values[row] += value;
		});

		return new Matrix(rows, 1, (row, col, index, value) -> {
			return values[index];
		});
	}

	public Matrix sumColumns() {
		double[] values = new double[cols];

		forEach((row, col, index, value) -> {
			values[col] += value;
		});

		return new Matrix(1, cols, (row, col, index, value) -> {
			return values[index];
		});
	}

	public double sum() {
		double[] sum = new double[1];

		forEach((row, col, index, value) -> {
			sum[0] += value;
		});

		return sum[0];
	}

	/**************************************************************************
	 * 
	 * Iterators
	 */

	public Matrix apply(Producer producer) {
		return new Matrix(rows, cols, (row, col, index, value) -> {
			return producer.produce(row, col, index, a[index]);
		});
	}
	
	public Matrix apply(RowColProducer producer) {
		return new Matrix(rows, cols, (row, col, index, value) -> {
			return producer.produce(row, col);
		});
	}
	
	public Matrix apply(ValueProducer producer) {
		return new Matrix(rows, cols, (row, col, index, value) -> {
			return producer.produce(a[index]);
		});
	}

	public Matrix modify(Producer producer) {
		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				a[index] = producer.produce(row, col, index, a[index]);
				++index;
			}
		}

		return this;
	}

	public void forEach(Consumer consumer) {

		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				consumer.consume(row, col, index, a[index++]);
			}
		}
	}

	/**************************************************************************
	 * 
	 * access methods
	 * @throws IOException 
	 */
	
	public void save(ObjectOutputStream os) throws IOException {
		os.writeInt(rows);
		os.writeInt(cols);
		os.writeObject(a);
	}
	
	public void load(ObjectInputStream os) throws ClassNotFoundException, IOException {
		rows = os.readInt();
		cols = os.readInt();
		a = (double[])os.readObject();
	}

	public Matrix getRow(int row) {
		Matrix result = new Matrix(1, cols);

		for (int i = 0; i < cols; i++) {
			result.a[i] = a[row * cols + i];
		}

		return result;
	}
	
	public double[] get() {
		return a;
	}

	public Matrix getCol(int col) {
		Matrix result = new Matrix(rows, 1);

		for (int i = 0; i < rows; i++) {
			result.a[i] = a[col + i * cols];
		}

		return result;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public double get(int index) {
		return a[index];
	}

	public double get(int row, int col) {
		return a[row * cols + col];
	}

	public void set(int index, double value) {
		a[index] = value;
	}

	/**************************************************************************
	 * 
	 * toString and equals methods
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(a);
		result = prime * result + cols;
		result = prime * result + rows;
		return result;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
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

		if (cols != other.getCols()) {
			return false;
		}

		if (rows != other.getRows()) {
			return false;
		}

		for (int i = 0; i < a.length; i++) {
			double a1 = a[i];
			double a2 = other.a[i];

			if (Math.abs(a1 - a2) > tolerance) {
				return false;
			}
		}

		if (cols != other.cols)
			return false;
		if (rows != other.rows)
			return false;
		return true;
	}

	public String toString(boolean showEntries) {
		if (showEntries) {
			return toString();
		} else {
			return rows + "x" + cols;
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		forEach((row, col, index, value) -> {

			if (col == MAX_PRINT && row < MAX_PRINT) {
				sb.append(" ... ");
			} else if (row == MAX_PRINT && col == 0) {
				sb.append("\n...");
				return;
			} else if (col < MAX_PRINT && row < MAX_PRINT) {
				if (col == 0) {
					sb.append("\n");
				}

				sb.append(String.format(NUMBER_FORMAT, value));
			}

		});

		return sb.toString();
	}

}
