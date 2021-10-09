package cave;

public class LossFunctions {
	public static Matrix meanSquared(Matrix input, Matrix expected) {
		
		Matrix difference = input.subtract(expected);
		
		double[] squaredAverage = new double[input.getCols()]; 
		double rows = input.getRows();
		
		difference.forEach((row, col, index, value)->{
			squaredAverage[col] += value * value/rows;
		});
		
		return new Matrix(1, input.getCols(), (row, col, index, value)->squaredAverage[col]);
	}
	
	public static Matrix categoricalCrossEntropy(Matrix input, Matrix expected) {
		return input.apply((row, col, index, value)->{
			return -expected.get(index) * Math.log(value);
		}).sumColumns();
	}
	
}
