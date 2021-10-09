package cave.matrix;

import org.junit.Test;

public class ScratchPad {

	@Test
	public void test() {
		Matrix m = new Matrix(3, 4, (row, col, index)->(row + 1) * 10 + (col + 1));
		
		System.out.println(m);
	}

}
