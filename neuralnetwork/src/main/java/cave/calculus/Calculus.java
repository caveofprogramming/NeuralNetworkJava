package cave.calculus;

import java.util.function.DoubleFunction;

public class Calculus {
	
	private static final double INC = 0.00001;
	
	public static double func1(double x) {
		return 3.7 * x + 5.3;
	}
	
	public static double func2(double x) {
		return x * x - 3.23;
	}
	
	public static double differentiate(DoubleFunction<Double> func, double x) {
		
		double output1 = func.apply(x);
		double output2 = func.apply(x + INC);
		
		
		return (output2 - output1)/INC;
	}

	public static void main(String[] args) {
		
		for(double x = -2; x < 2; x += 0.1) {
			
			double gradient = differentiate(Calculus::func2, x);
			System.out.printf("%.2f\t%.2f\n", x, gradient);
		}
	}
}
