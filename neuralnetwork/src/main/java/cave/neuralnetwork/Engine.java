package cave.neuralnetwork;

import java.util.LinkedList;
import java.util.Random;

import cave.matrix.Matrix;

public class Engine {
	private LinkedList<Transform> transforms = new LinkedList<>();
	private LinkedList<Matrix> weights = new LinkedList<>();
	private LinkedList<Matrix> biases = new LinkedList<>();

	public void add(Transform transform, double... params) {
		
		Random random = new Random();
		
		if(transform == Transform.DENSE) {
			int numberNeurons = (int)params[0];
			int weightsPerNeuron = weights.size() == 0 ? (int)params[1]: weights.getLast().getRows();
			
			Matrix weight = new Matrix(numberNeurons, weightsPerNeuron, i->random.nextGaussian());
			Matrix bias = new Matrix(numberNeurons, 1, i->random.nextGaussian());
			
			weights.add(weight);
			biases.add(bias);
		}
		transforms.add(transform);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		int weightIndex = 0;
		for (var t : transforms) {
			
			sb.append(t);
			
			if(t == Transform.DENSE) {
				sb.append(" ").append(weights.get(weightIndex).toString(false));
				
				weightIndex++;
			}
			
			sb.append("\n");
		}

		return sb.toString();
	}
}
