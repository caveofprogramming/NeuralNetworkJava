package cave;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cave.mnist.BatchData;
import cave.mnist.Loader;
import cave.mnist.MetaData;

public class NeuralNet {

	private Engine engine;
	private int threads = 6;
	private int epochs = 20;
	private double learningRate;
	
	private double initialLearningRate = 0.03;
	private double finalLearningRate = 0.005;
	private Byte weightLock = 0;

	public NeuralNet() {
		engine = new Engine();
	}
	
	public void setTransform(int index, Transform transform, double... params) {
		engine.setTransform(index, transform, params);
	}
	
	public Matrix evaluate(Matrix input) {
		var batchResult = engine.runForwards(input);
		
		return batchResult.getFinalOutput();
	}
	
	public double[] predict(double[] input) {
		
		Matrix m = new Matrix(input.length, 1, i->input[i]);
		
		var batchResult = engine.runForwards(m);
		
		return batchResult.getFinalOutput().get();
	}

	private BatchResult runBatch(Loader loader, boolean train) {

		MetaData metaData = loader.getMetaData();

		BatchData batchData = loader.readBatch();

		var inputValues = batchData.getInputBatch();
		var expectedValues = batchData.getExpectedBatch();

		if (inputValues == null) {
			System.out.println("\n" + metaData);
		}

		int inputRows = metaData.getInputSize();
		int expectedRows = metaData.getExpectedSize();
		int cols = metaData.getItemsRead();

		Matrix input = new Matrix(inputRows, cols, false, inputValues);
		Matrix expected = new Matrix(expectedRows, cols, false, expectedValues);

		var batchResult = engine.runForwards(input);

		if (train) {
			engine.runBackwards(batchResult, expected);

			synchronized (weightLock) {
				engine.adjust(batchResult, learningRate);
			}
		} else {
			engine.evaluate(expected, batchResult);
		}

		return batchResult;
	}

	private LinkedList<Future<BatchResult>> createBatchTasks(Loader loader, boolean train) {

		LinkedList<Future<BatchResult>> batches = new LinkedList<>();

		MetaData metaData = loader.open();
		var numberBatches = metaData.getNumberBatches();

		var executor = Executors.newFixedThreadPool(threads);

		for (int i = 0; i < numberBatches; i++) {
			batches.add(executor.submit(() -> runBatch(loader, train)));
		}

		executor.shutdown();

		return batches;
	}

	private void runEpoch(Loader loader, boolean train) {

		loader.open();

		var queue = createBatchTasks(loader, train);
		consumeBatchTasks(queue, train);

		loader.close();
	}

	private void consumeBatchTasks(LinkedList<Future<BatchResult>> batches, boolean train) {

		var numberBatches = batches.size();

		int index = 0;

		double averageLoss = 0;
		double averageCorrect = 0;

		for (var batch : batches) {

			try {
				var batchResult = batch.get();

				if (!train) {
					averageLoss += batchResult.getLoss();
					averageCorrect += batchResult.getPercentCorrect();
				}
			} catch (Exception e) {
				throw new RuntimeException("Execution error.", e);
			}

			int printDot = numberBatches / 30;

			if (train && index++ % printDot == 0) {
				System.out.print(".");
			}
		}

		if (!train) {
			averageLoss /= batches.size();
			averageCorrect /= batches.size();

			System.out.printf(" Loss: %4.2f — %% correct: %4.2f", averageLoss, averageCorrect);
		}
	}

	public void fit(Loader trainLoader, Loader evalLoader) {
		
		learningRate = initialLearningRate;

		for (int epoch = 0; epoch < epochs; epoch++) {

			System.out.printf("Epoch %3d ", epoch);

			runEpoch(trainLoader, true);

			if (evalLoader != null)
				runEpoch(evalLoader, false);

			System.out.println();
			
			learningRate -= (initialLearningRate - finalLearningRate)/epochs;
		}
		
	}

	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}

	public void add(Transform transform, double... params) {
		engine.add(transform, params);
	}
	
	public boolean save(String filename) {
		try(var ds = new ObjectOutputStream(new FileOutputStream(filename))) {
			
			ds.writeInt(threads);
			ds.writeInt(epochs);
			ds.writeDouble(initialLearningRate);
			ds.writeDouble(finalLearningRate);
			
			ds.writeObject(engine);
				
		} catch (IOException e) {
			System.out.println("Unable to create file " + filename);
			return false;
		}
		
		return true;
	}
	
	public boolean load(String filename) {
		try(var ds = new ObjectInputStream(new FileInputStream(filename))) {
			
			threads = ds.readInt();
			epochs = ds.readInt();
			initialLearningRate = ds.readDouble();
			finalLearningRate = ds.readDouble();
			
			engine = (Engine)ds.readObject();

		} catch (IOException|ClassNotFoundException e) {
			return false;
		}
		
		return true;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Epochs: %d", epochs)).append("\n");
		sb.append(String.format("Threads: %d", threads)).append("\n");
		//sb.append(String.format("Dropout: %.3f", engine.getDropout())).append("\n");
		sb.append(String.format("Initial learning rate: %.5f",  initialLearningRate)).append("\n");
		sb.append(String.format("Final learning rate: %.5f", finalLearningRate)).append("\n");
		sb.append("\n");
		sb.append(engine);
		
		return sb.toString();
	}

}
