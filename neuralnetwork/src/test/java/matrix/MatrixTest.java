package matrix;

import org.junit.jupiter.api.Test;

import cave.matrix.Matrix;

class MatrixTest {

	@Test
	public void constructionTest() {
		Matrix m = new Matrix(3, 4, i->i*2);
		
		System.out.println(m);
	}

}
