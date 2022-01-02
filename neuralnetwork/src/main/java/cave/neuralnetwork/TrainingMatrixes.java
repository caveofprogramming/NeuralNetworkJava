package cave.neuralnetwork;

import cave.matrix.Matrix;

public class TrainingMatrixes {
	private Matrix input;
	private Matrix output;
	
	public TrainingMatrixes(Matrix input, Matrix output) {
		this.input = input;
		this.output = output;
	}

	public Matrix getInput() {
		return input;
	}

	public void setInput(Matrix input) {
		this.input = input;
	}

	public Matrix getOutput() {
		return output;
	}

	public void setOutput(Matrix output) {
		this.output = output;
	}
	
	
}
