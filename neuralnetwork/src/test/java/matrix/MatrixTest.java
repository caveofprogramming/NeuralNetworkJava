package matrix;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import cave.matrix.Matrix;

class MatrixTest {

	@Test
	public void testMultiplyDouble() {
		Matrix m = new Matrix(3, 4, i -> 0.5 * (i - 6));
		
		double x = 0.5;
		
		Matrix result = m.apply((index, value)->x * value);
				
		System.out.println(m);
		System.out.println(result);
	}

	@Test
	public void testToString() {
		Matrix m = new Matrix(3, 4, i -> i * 2);

		String text = m.toString();

		double[] expected = new double[12];

		for (int i = 0; i < expected.length; i++) {
			expected[i] = i * 2;
		}

		var rows = text.split("\n");

		assertTrue(rows.length == 3);

		int index = 0;

		for (var row : rows) {
			var values = row.split("\\s+");

			for (var textValue : values) {

				if (textValue.length() == 0) {
					continue;
				}

				var doubleValue = Double.valueOf(textValue);

				assertTrue(Math.abs(doubleValue - expected[index]) < 0.0001);

				index++;
			}
		}
	}

}
