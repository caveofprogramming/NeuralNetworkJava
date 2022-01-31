package cave.neuralnetwork;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

import cave.matrix.Matrix;

public class Engine implements Serializable {
	private static final long serialVersionUID = 1L;
	private LinkedList<Transform> transforms = new LinkedList<>();
	private LinkedList<Matrix> weights = new LinkedList<>();
	private LinkedList<Matrix> biases = new LinkedList<>();
	
	private LossFunction lossFunction = LossFunction.CROSSENTROPY;
	private double scaleInitialWeights = 1;
	
	private boolean storeInputError = false;
	
	public void setScaleInitialWeights(double scale) {
		scaleInitialWeights = scale;
		
		if(weights.size() != 0) {
			throw new RuntimeException("Must call setScaleInitialWeights BEFORE adding transforms!");
		}
	}
	
	public void evaluate(BatchResult batchResult, Matrix expected) {
		
		if(lossFunction != LossFunction.CROSSENTROPY) {
			throw new UnsupportedOperationException("Only cross entropy loss is supported");
		}
		
		double loss = LossFunctions.crossEntropy(expected, batchResult.getOutput()).averageColumn().get(0);
		
		Matrix predictions = batchResult.getOutput().getGreatestRowNumbers();
		Matrix actual = expected.getGreatestRowNumbers();
		
		int correct = 0;
		
		for(int i = 0; i < actual.getCols(); i++) {
			if((int)actual.get(i) == (int)predictions.get(i)) {
				++correct;
			}
		}
		
		double percentCorrect = (100.0 * correct)/actual.getCols();
		
		batchResult.setLoss(loss);
		batchResult.setPercentCorrect(percentCorrect);
	}
	
	BatchResult runForwards(Matrix input) {
		
		BatchResult batchResult = new BatchResult();
		Matrix output = input;
		
		int denseIndex = 0;
		
		batchResult.addIo(output);
		
		for(var t: transforms) {
			if(t == Transform.DENSE) {
				
				batchResult.addWeightInput(output);
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
	
	public void adjust(BatchResult batchResult, double learningRate) {
		var weightInputs = batchResult.getWeightInputs();
		var weightErrors = batchResult.getWeightErrors();
		
		assert weightInputs.size() == weightErrors.size();
		assert weightInputs.size() == weights.size();
		
		for(int i = 0; i < weights.size(); i++) {
			var weight = weights.get(i);
			var bias = biases.get(i);
			var error = weightErrors.get(i);
			var input = weightInputs.get(i);
			
			assert weight.getCols() == input.getRows();
			
			var weightAdjust = error.multiply(input.transpose());
			var biasAdjust = error.averageColumn();
			
			double rate = learningRate/input.getCols();
			
			weight.modify((index, value)->value - rate * weightAdjust.get(index));
			bias.modify((row, col, value)->value - learningRate * biasAdjust.get(row));
		}
		
	}
	
	public void runBackwards(BatchResult batchResult, Matrix expected) {
		
		var transformsIt = transforms.descendingIterator();
		
		if(lossFunction != LossFunction.CROSSENTROPY || transforms.getLast() != Transform.SOFTMAX) {
			throw new UnsupportedOperationException("Loss function must be cross entropy and last transform must be softmax");
		}
		
		var ioIt = batchResult.getIo().descendingIterator();
		var weightIt = weights.descendingIterator();
		Matrix softmaxOutput = ioIt.next();
		Matrix error = softmaxOutput.apply((index, value)->value - expected.get(index));
		
		while(transformsIt.hasNext()) {
			Transform transform = transformsIt.next();
			
			Matrix input = ioIt.next();
			
			switch(transform) {
			case DENSE:
				Matrix weight = weightIt.next();
				
				batchResult.addWeightError(error);
				
				if(weightIt.hasNext() || storeInputError) {
					error = weight.transpose().multiply(error);
				}
				break;
			case RELU:
				error = error.apply((index, value)->input.get(index) > 0 ? value: 0);
				break;
			case SOFTMAX:
				break;
			default:
				throw new UnsupportedOperationException("Not implemented");
			}
			
			//System.out.println(transform);
		}
		
		if(storeInputError) {
			batchResult.setInputError(error);
		}
	}

	public void add(Transform transform, double... params) {
		
		Random random = new Random();
		
		if(transform == Transform.DENSE) {
			int numberNeurons = (int)params[0];
			int weightsPerNeuron = weights.size() == 0 ? (int)params[1]: weights.getLast().getRows();
			
			Matrix weight = new Matrix(numberNeurons, weightsPerNeuron, i->scaleInitialWeights * random.nextGaussian());
			Matrix bias = new Matrix(numberNeurons, 1, i->0);
			
			weights.add(weight);
			biases.add(bias);
		}
		transforms.add(transform);
	}
	

	public void setStoreInputError(boolean storeInputError) {
		this.storeInputError = storeInputError;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Scale initial weights: %.3f\n", scaleInitialWeights));
		
		sb.append("\nTransforms:\n");

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
