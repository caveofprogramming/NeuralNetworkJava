package cave;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Engine implements Serializable {
	private static final long serialVersionUID = 1L;
	private LinkedList<Transform> transforms = new LinkedList<>();
	private LinkedList<Matrix> weights = new LinkedList<>();
	private LinkedList<Matrix> biases = new LinkedList<>();

	private LossFunction lossFunction = LossFunction.CATEGORICAL_CROSS_ENTROPY;

	private boolean backpropToInput = false;
	private Random random = new Random();

	double dropout = 0.45;

	public void evaluate(Matrix expected, BatchResult batchResult) {

		var output = batchResult.getFinalOutput();

		assert output.getCols() == expected.getCols();
		assert output.getRows() == expected.getRows();

		if (lossFunction == LossFunction.CATEGORICAL_CROSS_ENTROPY) {
			batchResult.setLoss(LossFunctions.categoricalCrossEntropy(output, expected).averageColumns().get(0));
		} else {
			throw new UnsupportedOperationException();
		}

		expected.forEach((row, col, index, value) -> {
			if (Math.abs(value - 1.0) < 0.1) {
				if (output.get(index) > 0.5) {
					batchResult.incCorrect();
				} else {
					batchResult.incIncorrect();
				}
			}
		});
	}

	public BatchResult runForwards(Matrix input) {

		int weightIndex = 0;

		var batchResult = new BatchResult();

		batchResult.addIo(input);

		for (var transform : transforms) {

			if (transform == Transform.WEIGHTEDSUM) {

				Matrix weight = weights.get(weightIndex);
				Matrix bias = biases.get(weightIndex);

				++weightIndex;

				batchResult.addWeightInput(input);

				input = weight.multiply(input).modify((row, col, index, value) -> bias.get(row) + value);

			} else if (transform == Transform.SOFTMAX) {
				input = input.softmax();
			} else if (transform == Transform.RELU) {
				input = input.reLu();
			} else if (transform == Transform.DROPOUT) {
				boolean[] dropouts = new boolean[input.getRows()];
				
				double compensate = 1.0 / (1.0 - dropout);
				
				for(int i = 0; i < dropouts.length; i++) {
					if(random.nextDouble() < dropout) {
						dropouts[i] = true;
					}
				}
				
				batchResult.addDropout(dropouts);
				
				input = input.apply((row, col, index, value)->dropouts[row] ? 0: compensate * value);
			}

			batchResult.addIo(input);
		}

		return batchResult;
	}

	public void runBackwards(BatchResult batchResult, Matrix expected) {

		Matrix error = null;

		LinkedList<Matrix> weightErrors = batchResult.getWeightErrors();

		var transformsIt = transforms.descendingIterator();
		var weightIt = weights.descendingIterator();
		var outputsIt = batchResult.getIo().descendingIterator();
		var dropoutsIt = batchResult.getDropouts().descendingIterator();

		Matrix finalOutput = batchResult.getFinalOutput();

		if (lossFunction == LossFunction.CATEGORICAL_CROSS_ENTROPY && transforms.getLast() == Transform.SOFTMAX) {
			error = finalOutput.subtract(expected);

			transformsIt.next();
			outputsIt.next();
		} else if (lossFunction == LossFunction.MEAN_SQUARES && transforms.getLast() == Transform.RELU) {

			double factor = 2.0 / finalOutput.getRows();
			error = finalOutput.subtract(expected).modify((row, col, index, value) -> factor * value);
		} 
		else {
			throw new UnsupportedOperationException("Backprop not implemented for this combination of transforms.");
		}

		while (transformsIt.hasNext()) {
			Transform transform = transformsIt.next();
			Matrix output = outputsIt.next();

			if (transform == Transform.WEIGHTEDSUM) {

				weightErrors.addFirst(error);
				var weight = weightIt.next();

				if (weightIt.hasNext() || backpropToInput) {
					error = weight.transpose().multiply(error);
				}
			} else if (transform == Transform.RELU) {
				error = error.apply((row, col, index, value) -> {
					return output.get(index) > 0 ? value : 0;
				});
			} else if (transform == Transform.DROPOUT) {
				
				double compensate = 1.0 / (1.0 - dropout);
				
				var dropouts = dropoutsIt.next();
						
				error = error.apply((row, col, index, value)->dropouts[row] ? 0: compensate * value);		
			}
		}

		if (backpropToInput) {
			batchResult.setInitialError(error);
		}
	}

	public void adjust(BatchResult batchResult, double learningRate) {

		var io = batchResult.getWeightInputs();
		var errors = batchResult.getWeightErrors();

		assert io.size() == errors.size();
		assert weights.size() == errors.size();

		for (int i = 0; i < weights.size(); i++) {
			var weight = weights.get(i);
			var bias = biases.get(i);
			var input = io.get(i);
			var error = errors.get(i);

			double rate = learningRate / input.getCols();
			var weightAdjust = error.multiply(input.transpose());

			assert error.getRows() == bias.getRows();
			assert bias.getCols() == 1;

			weight.modify((row, col, index, value) -> value - weightAdjust.get(index) * rate);
			bias.modify((row, col, index, value) -> value - error.get(index) * rate);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (var transform : transforms) {
			sb.append(transform);
			sb.append("\n");
		}

		return sb.toString();
	}

	public void setBackpropToInput(boolean backpropToInput) {
		this.backpropToInput = backpropToInput;
	}

	public void add(Transform transform, double... params) {

		transforms.add(transform);

		if (transform == Transform.WEIGHTEDSUM) {
			var nodes = (int) params[0];
			var connections = (int) params[1];
			weights.add(new Matrix(nodes, connections, (row, col, index, value) -> 0.2 * random.nextGaussian()));
			biases.add(new Matrix(nodes, 1));
		} 
	}
	
	public double getDropout() {
		return dropout;
	}
	
	public List<Matrix> getWeights() {
		return weights;
	}
	
	public List<Matrix> getBiases() {
		return biases;
	}
	
	public List<Transform> getTransforms() {
		return transforms;
	}

	public void setTransform(int index, Transform transform, double... params) {
		transforms.set(index, transform);

	}
}
