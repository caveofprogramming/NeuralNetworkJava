package cave;

import java.util.LinkedList;
import java.util.List;

public class BatchResult {
	private Matrix initialError;
	private LinkedList<Matrix> io = new LinkedList<>();
	
	private LinkedList<Matrix> weightErrors = new LinkedList<>();
	private LinkedList<Matrix> weightInputs = new LinkedList<>();
	private LinkedList<boolean[]> dropouts = new LinkedList<>();
	
	private double loss;
	private int correct = 0;
	private int incorrect = 0;
	
	
	public void addDropout(boolean [] dropout) {
		dropouts.add(dropout);
	}
	
	public LinkedList<boolean[]> getDropouts() {
		return dropouts;
	}
	
	public void addIo(Matrix input) {
		io.add(input);
	}

	public void addWeightInput(Matrix input) {
		weightInputs.add(input);
	}

	public LinkedList<Matrix> getIo() {
		return io;
	}
	
	public Matrix getFinalOutput() {
		return io.getLast();
	}

	public LinkedList<Matrix> getWeightErrors() {
		return weightErrors;
	}

	public void setInitialError(Matrix initialError) {
		this.initialError = initialError;
	}

	public Matrix getInitialError() {
		return initialError;
	}

	public LinkedList<Matrix> getWeightInputs() {
		return weightInputs;
	}
	
	public double getLoss() {
		return loss;
	}

	public void setLoss(double loss) {
		this.loss = loss;
	}

	public double getPercentCorrect() {
		return (100.0 * correct) / (correct + incorrect);
	}
	
	public void incCorrect() {
		correct++;
	}
	
	public void incIncorrect() {
		incorrect++;
	}

}
