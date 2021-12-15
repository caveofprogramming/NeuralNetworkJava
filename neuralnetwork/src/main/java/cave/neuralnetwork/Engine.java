package cave.neuralnetwork;

import java.util.LinkedList;
import java.util.Random;

import cave.matrix.Matrix;

public class Engine {
	private LinkedList<Transform> transforms = new LinkedList<>();
	private LinkedList<Matrix> weights = new LinkedList<>();
	private LinkedList<Matrix> biases = new LinkedList<>();
	
	BatchResult runForwards(Matrix input) {
		
		BatchResult batchResult = new BatchResult();
		Matrix output = input;
		
		int denseIndex = 0;
		
		batchResult.addIo(output);
		
		for(var t: transforms) {
			if(t == Transform.DENSE) {
				
				Matrix weight = weights.get(denseIndex);
				Matrix bias = biases.get(denseIndex);
				
				output = weight.multiply(output).modify((row, col, value) -> value + bias.get(row));
						
				++denseIndex;
			}
			else if(t == Transform.RELU) {
				output = output.modify(value -> value > 0 ? value: 0);
			}
			else if(t == Transform.SOFTMAX) {
				output = output.softmax();
			}
			
			batchResult.addIo(output);
		}
		
		return batchResult;
	}
	
	public Matrix runBackwards(BatchResult batchResult, Matrix expected) {
		
		var transformsIt = transforms.descendingIterator();
		
		while(transformsIt.hasNext()) {
			Transform transform = transformsIt.next();
			
			switch(transform) {
			case DENSE:
				break;
			case RELU:
				break;
			case SOFTMAX:
				break;
			default:
				throw new UnsupportedOperationException("Not implemented");
			}
			
			System.out.println(transform);
		}
		return null;
	}

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
