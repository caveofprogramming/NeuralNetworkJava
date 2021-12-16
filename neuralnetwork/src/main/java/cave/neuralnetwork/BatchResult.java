package cave.neuralnetwork;

import java.util.LinkedList;

import cave.matrix.Matrix;

public class BatchResult {
	private LinkedList<Matrix> io = new LinkedList<>();
	private LinkedList<Matrix> weightErrors = new LinkedList<>();
	private Matrix inputError;
	
	public LinkedList<Matrix> getIo() {
		return io;
	}
	
	public void addIo(Matrix m) {
		io.add(m);
	}

	public LinkedList<Matrix> getWeightErrors() {
		return weightErrors;
	}

	public void addWeightError(Matrix weightError) {
		weightErrors.addFirst(weightError);
	}

	public Matrix getInputError() {
		return inputError;
	}

	public void setInputError(Matrix inputError) {
		this.inputError = inputError;
	}
	
	
}
