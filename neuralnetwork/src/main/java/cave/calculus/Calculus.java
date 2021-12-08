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
	
	public static double func3(double x) {
		return func2(func1(x));
	}
	
	public static double differentiate(DoubleFunction<Double> func, double x) {
		
		double output1 = func.apply(x);
		double output2 = func.apply(x + INC);
		
		
		return (output2 - output1)/INC;
	}

	public static void main(String[] args) {
		
		double x = 3.64;
		double y = func1(x);
		double z = func2(y);
		
		/*
		System.out.println(x);
		System.out.println(y);
		System.out.println(z);
		System.out.println(func2(func1(x)));
		System.out.println(func3(x));
		*/
		
		double dydx = differentiate(Calculus::func1, x);
		double dzdy = differentiate(Calculus::func2, y);
		double dzdx = differentiate(Calculus::func3, x);
		
		System.out.println(dydx);
		System.out.println(dzdy);
		System.out.println(dzdx);
		System.out.println(dzdy * dydx);
		
		
	}
}
