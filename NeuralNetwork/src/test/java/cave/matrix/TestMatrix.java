package cave.matrix;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestMatrix {
	
	private static final double TOLERANCE = 0.00001;
	
	private static boolean equal(double v1, double v2) {
		return Math.abs(v1 - v2) < TOLERANCE;
	}
	

	@Test
	public void testToString() {
		
		Matrix m1 = new Matrix(5, 4, i -> i);

		String s1 = m1.toString();
		
		assertTrue(s1.split("\\n").length == 5);
		
		int index = 0;
		
		for(var s: s1.split("\\s+"))  {
			
			if(s.length() == 0) {
				continue;
			}
			
			double value = Double.valueOf(s);
			
			assertTrue(equal(value, index));
			
			++index;
		}
		
		assertTrue(index == 20);
	}
}
