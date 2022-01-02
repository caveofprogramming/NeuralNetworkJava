package cave.neuralnetwork;

import java.util.LinkedList;

import cave.matrix.Matrix;

public class BatchResult {
	private LinkedList<Matrix> io = new LinkedList<>();
	private LinkedList<Matrix> weightErrors = new LinkedList<>();
	private LinkedList<Matrix> weightInputs = new LinkedList<>();
	
	private Matrix inputError;
	private double loss;
	private double percentCorrect;
	
	public void addWeightInput(Matrix input) {
		weightInputs.add(input);
	}
	
	public LinkedList<Matrix> getWeightInputs() {
		return weightInputs;
	}
	
	public LinkedList<Matrix> getIo() {
		return io;
	}
	
	public void addIo(Matrix m) {
		io.add(m);
	}
	
	public Matrix getOutput() {
		return io.getLast();
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

	public void setLoss(double loss) {
		this.loss = loss;
	}
	
	public double getLoss() {
		return loss;
	}

	public void setPercentCorrect(double percentCorrect) {
		this.percentCorrect = percentCorrect;
	}
	
	public double getPercentCorrect() {
		return percentCorrect;
	}
	
	
}
