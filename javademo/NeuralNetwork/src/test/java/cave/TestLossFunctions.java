package cave;

import static org.junit.Assert.*;

import java.util.stream.Stream;

import org.junit.Test;

public class TestLossFunctions {
	
	public static final double TOLERANCE = 0.00001;

	@Test
	public void testMeanSquared() {
		
		// @formatter:off
		String inputString = 	"-1.371071   -0.621044   -0.124718 " + 
				  				"-0.028646   -1.495391   -0.482230 "+
				  				"+0.391426   +0.466387   -0.358028 ";
		
		double[] expectedValues = 	{
				1, 0, 0,
				0, 0, 1,
				0, 1, 0,
		};
		// @formatter:on
		
		double[] inputValues = Stream.of(inputString.split("\\s+")).mapToDouble(s->Double.parseDouble(s)).toArray();
		
		double[] resultValues = {+1.925338, +0.968878, +0.780248};
		
		
		Matrix input = new Matrix(3, 3, (row, col, i, value)->inputValues[i]);
		Matrix expected = new Matrix(3, 3, (row, col, i, value)->expectedValues[i]);
		Matrix expectedResult = new Matrix(1, 3, (row, col, i, value)->resultValues[i]);
		
		Matrix output = LossFunctions.meanSquared(input, expected);

		expectedResult.setTolerance(TOLERANCE);
		
		assertTrue(expectedResult.equals(output));

	}

}
