package cave.neuralnetwork;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	// inputs
        double[] x = {0, 1};
        
        // weights
        double[] w = {0.5, 0.5};
        
        // bias
        double b = 0.5;
        
        // weighted sum
        double z = 0.0;
        
        for(int i = 0; i < x.length; i++) {
        	z += x[i] * w[i];
        }
        
        z += b;
        
        // activation function
        double a = z > 0 ? 1.0: 0.0;
        
        // print activation (output)
        System.out.println(a);
    }
}
