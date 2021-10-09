package cave;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class TestMatrix {

	private static double TOLERANCE = 0.0000001;
	private Random random = new Random();

	@Test
	public void testArrayConstructor() {

		double[] values = { 1, 2, 3, 4, 5, 6 };

		Matrix m1 = new Matrix(3, 2, true, values);
		Matrix m2 = new Matrix(3, 2, false, values);

		Matrix expected1 = new Matrix(3, 2, i -> i + 1);
		Matrix expected2 = new Matrix(3, 2, (row, col, index, value) -> (row + 1) + col * 3);

		assert (expected1.equals(m1));
		assert (expected2.equals(m2));
	}

	@Test
	public void testGetCol() {

		Matrix m = new Matrix(3, 5, i -> random.nextDouble());

		Matrix result = new Matrix(3, 5, (row, col, index, value) -> {
			return m.getCol(col).get(row);
		});

		m.setTolerance(TOLERANCE);

		assertTrue(m.equals(result));
	}

	@Test
	public void testGetRow() {

		Matrix m = new Matrix(3, 5, i -> random.nextDouble());

		Matrix result = new Matrix(3, 5, (row, col, index, value) -> {
			return m.getRow(row).get(col);
		});

		m.setTolerance(TOLERANCE);

		assertTrue(m.equals(result));
	}

	@Test
	public void testToString() {

		Matrix m = new Matrix(3, 4, (row, col, index, value) -> {
			return (index - 20) / 2.0;
		});

		String result = m.toString(true);

		String[] lines = result.strip().split("\\n");

		int index = 0;

		for (var line : lines) {
			String[] numbers = line.strip().split("\\s+");

			for (var number : numbers) {
				double value = Double.valueOf(number);
				double expectedValue = (index - 20) / 2.0;

				assertTrue("Not printed correctly: " + result, Math.abs(value - expectedValue) < TOLERANCE);

				index++;
			}

		}
	}

	@Test
	public void testGet() {

		int rows = 3;
		int cols = 4;

		Matrix m = new Matrix(rows, cols, (row, col, index, value) -> index);

		int index = 0;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				double value = m.get(row, col);

				double expected = index++;

				assertTrue(Math.abs(value - expected) < TOLERANCE);
			}
		}
	}

	@Test
	public void testReLu() {
		Matrix input = Util.generateInputMatrix(6, 8);

		Matrix output = input.reLu();

		output.forEach((row, col, index, value) -> {
			double inputValue = input.get(index);

			double expectedValue = 0;

			if (inputValue > 0) {
				expectedValue = inputValue;
			}

			assertTrue(Math.abs(expectedValue - value) < TOLERANCE);
		});
	}

	@Test
	public void testSoftmax() {

		Matrix input = Util.generateInputMatrix(6, 8);

		Matrix output = input.softmax();

		double sum = output.sum();

		assertTrue(sum > 0);

		Matrix columnSum = output.sumColumns();

		columnSum.forEach((row, col, index, value) -> {
			assertTrue(Math.abs(value - 1.0) < TOLERANCE);
		});
	}

	@Test
	public void testAddToElement() {

		double inc = 0.5;

		double[] values = { 1, 3, 4, 0.5, 4, 7.4, -0.6, 3, 9 };
		double[] expectedValues = { 1, 3, 4, 0.5, 4, 7.9, -0.6, 3, 9 };

		Matrix m = new Matrix(3, 3, (row, col, index, value) -> values[index]);
		Matrix expected = new Matrix(3, 3, (row, col, index, value) -> expectedValues[index]);

		Matrix result = m.addToElement(1, 2, inc);

		assertTrue(expected.equals(result));
	}

	@Test
	public void testAdd() {

		int rows = 3;
		int cols = 4;

		double[] values = new double[rows * cols];

		Matrix m1 = new Matrix(rows, cols, (row, col, index, value) -> {

			double a = index - rows * cols * 0.5;

			values[index] += a;

			return a;
		});

		Matrix m2 = new Matrix(rows, cols, (row, col, index, value) -> {
			double a = 2.0 * index - 10;

			values[index] += a;

			return a;
		});

		Matrix expected = new Matrix(rows, cols, (row, col, index, value) -> {
			return values[index];
		});

		Matrix result = m1.add(m2);

		assertTrue(expected.equals(result));
	}

	@Test
	public void testAddToAllColumns() {
		Matrix m1 = new Matrix(3, 4, (row, col, index, value) -> index);

		Matrix m2 = new Matrix(3, 1, (row, col, index, value) -> index + 2);

		Matrix expected = new Matrix(3, 4, (row, col, index, value) -> {
			return row * 5 + 2 + col;
		});

		Matrix result = m1.addToAllColumns(m2, false);

		assertTrue(expected.equals(result));
	}

	@Test
	public void testSubtract() {

		int rows = 3;
		int cols = 4;

		double[] values = new double[rows * cols];

		Matrix m1 = new Matrix(rows, cols, (row, col, index, value) -> {

			double a = index - rows * cols * 0.5;

			values[index] += a;

			return a;
		});

		Matrix m2 = new Matrix(rows, cols, (row, col, index, value) -> {
			double a = 2.0 * index - 10;

			values[index] -= a;

			return a;
		});

		Matrix expected = new Matrix(rows, cols, (row, col, index, value) -> {
			return values[index];
		});

		Matrix result = m1.subtract(m2);

		assertTrue(expected.equals(result));
	}

	@Test
	public void testMultiply() {

		int rows = 3;
		int mid = 2;
		int cols = 4;

		double[] expectedValues = { +4.000000, +5.000000, +6.000000, +7.000000, +12.000000, +17.000000, +22.000000,
				+27.000000, +20.000000, +29.000000, +38.000000, +47.000000 };

		Matrix m1 = new Matrix(rows, mid, (row, col, index, value) -> {
			return index;
		});

		Matrix m2 = new Matrix(mid, cols, (row, col, index, value) -> {
			return index;
		});

		Matrix expected = new Matrix(rows, cols, (row, col, index, value) -> {
			return expectedValues[index];
		});

		Matrix result = m1.multiply(m2);

		assertTrue(expected.equals(result));
	}

	@Test
	public void testSum() {
		Matrix m1 = new Matrix(3, 3, (row, col, index, value) -> {
			return index;
		});

		double sum = m1.sum();

		assert (Math.abs(sum - 36) < 0.000001);
	}

	@Test
	public void testAverageColumns() {

		// @formatter:off
		double[] values = new double[] { 
				4, 2,
				5, 7, 
				10, 8, };
		// @formatter:on

		double[] expectedValues = new double[] { 3, 6, 9 };

		Matrix result = new Matrix(3, 2, index -> values[index]).averageColumns();
		Matrix expected = new Matrix(3, 1, index -> expectedValues[index]);

		expected.setTolerance(TOLERANCE);

		assertTrue(expected.equals(result));
	}

	@Test
	public void testSumColumns() {
		Matrix m1 = new Matrix(3, 3, (row, col, index, value) -> {
			return index;
		});

		Matrix result = m1.sumColumns();

		Matrix expected = new Matrix(1, 3, (row, col, index, value) -> {
			return 3 * (col + 3);
		});
		assertTrue(expected.equals(result));
	}

	@Test
	public void testMaxValuesByColumn() {

		double[] maxValues = new double[6];

		Matrix m = new Matrix(5, 6, (row, col, index, value) -> {
			return random.nextInt();
		});

		m.forEach((row, col, index, value) -> {
			if (value > maxValues[col]) {
				maxValues[col] = value;
			}
		});

		Matrix result = m.maxValuesByColumn();

		result.forEach((row, col, index, value) -> {
			assertTrue(Math.abs(value - maxValues[col]) < TOLERANCE);
		});
	}

	@Test
	public void testSumRows() {
		Matrix m1 = new Matrix(3, 3, (row, col, index, value) -> {
			return index;
		});

		Matrix result = m1.sumRows();

		Matrix expected = new Matrix(3, 1, (row, col, index, value) -> {
			return (3 + (row * 9));
		});

		assertTrue(expected.equals(result));
	}

	@Test
	public void testMultiplySpeed() {

		int rows = 200;
		int cols = 240;
		int mid = 150;

		Matrix m1 = new Matrix(rows, mid, (row, col, index, value) -> {
			return index;
		});

		Matrix m2 = new Matrix(mid, cols, (row, col, index, value) -> {
			return index;
		});

		long start = System.currentTimeMillis();
		m1.multiply(m2);
		long end = System.currentTimeMillis();

		System.out.printf("Matrix multiplication speed test: %d ms\n", end - start);
	}
	

	@Test
	public void tesTransposeSpeed() {

		int rows = 5000;
		int cols = 3040;

		rows = 5;
		cols = 3;

		Matrix m1 = new Matrix(rows, cols, (row, col, index, value) -> {
			return index;
		});

		long start = System.currentTimeMillis();

		Matrix result = m1;
		for (int i = 0; i < 100000; i++) {
			result = result.transpose();
		}
		long end = System.currentTimeMillis();

		System.out.printf("Matrix transpose speed test: %d ms\n", end - start);
	}


	@Test
	public void testTranspose() {

		int rows = 3;
		int cols = 2;

		Matrix m1 = new Matrix(rows, cols, (row, col, index, value) -> {
			return index;
		});

		Matrix result = m1.transpose();

		double[] expectedValues = { 0, 2, 4, 1, 3, 5 };

		Matrix expected = new Matrix(cols, rows, (row, col, index, value) -> {
			return expectedValues[index];
		});

		assertTrue(expected.equals(result));
	}


}
