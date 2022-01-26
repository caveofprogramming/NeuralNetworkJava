package cave.neuralnetwork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cave.matrix.Matrix;
import cave.neuralnetwork.loader.BatchData;
import cave.neuralnetwork.loader.Loader;
import cave.neuralnetwork.loader.MetaData;

public class NeuralNetwork implements Serializable {
	private static final long serialVersionUID = 1L;

	private Engine engine;

	private int epochs = 20;
	private double initialLearningRate = 0.01;
	private double finalLearningRate = 0.001;
	private int threads = 2;
	
	transient private double learningRate;
	transient private Object lock = new Object();


	public NeuralNetwork() {
		engine = new Engine();
	}
	
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	public void setScaleInitialWeights(double scale) {
		engine.setScaleInitialWeights(scale);
	}

	public void add(Transform transform, double... params) {
		engine.add(transform, params);
	}

	public void setLearningRates(double initialLearningRate, double finalLearningRate) {
		this.initialLearningRate = initialLearningRate;
		this.finalLearningRate = finalLearningRate;
	}

	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}
	
	public double[] predict(double[] inputData) {
		
		Matrix input = new Matrix(inputData.length, 1, i->inputData[i]);
		
		BatchResult batchResult = engine.runForwards(input);
		return batchResult.getOutput().get();
	}

	public void fit(Loader trainLoader, Loader evalLoader) {

		learningRate = initialLearningRate;

		for (int epoch = 0; epoch < epochs; epoch++) {

			System.out.printf("Epoch %3d ", epoch + 1);

			runEpoch(trainLoader, true);

			if (evalLoader != null) {
				runEpoch(evalLoader, false);
			}
			
			System.out.println();

			learningRate -= (initialLearningRate - finalLearningRate) / epochs;
		}
	}

	private void runEpoch(Loader loader, boolean trainingMode) {

		loader.open();

		var queue = createBatchTasks(loader, trainingMode);
		consumeBatchTasks(queue, trainingMode);

		loader.close();
	}

	private void consumeBatchTasks(LinkedList<Future<BatchResult>> batches, boolean trainingMode) {
		
		var numberBatches = batches.size();
		
		int index = 0;
		
		double averageLoss = 0;
		double averagePercentCorrect = 0;
			
		for(var batch: batches) {
			try {
				var batchResult = batch.get();
				
				if(!trainingMode) {
					averageLoss += batchResult.getLoss();
					averagePercentCorrect += batchResult.getPercentCorrect();
				}
			} catch (Exception e) {
				throw new RuntimeException("Execution error: ", e);
			} 
			
			int printDot = numberBatches/30;
			
			if(trainingMode && index++ % printDot == 0) {
				System.out.print(".");
			}
		}
		
		if(!trainingMode) {
			averageLoss /= batches.size();
			averagePercentCorrect /= batches.size();
			
			System.out.printf("Loss: %.3f -- Percent correct: %.2f", averageLoss, averagePercentCorrect);
		}

	}

	private LinkedList<Future<BatchResult>> createBatchTasks(Loader loader, boolean trainingMode) {
		
		LinkedList<Future<BatchResult>> batches = new LinkedList<>();

		MetaData metaData = loader.getMetaData();
		int numberBatches = metaData.getNumberBatches();
		
		var executor = Executors.newFixedThreadPool(threads);

		for (int i = 0; i < numberBatches; i++) {
			batches.add(executor.submit(()->runBatch(loader, trainingMode)));
		}
		
		executor.shutdown();

		return batches;
	}

	private BatchResult runBatch(Loader loader, boolean trainingMode) {

		MetaData metaData = loader.getMetaData();

		BatchData batchData = loader.readBatch();

		int itemsRead = metaData.getItemsRead();

		int inputSize = metaData.getInputSize();
		int expectedSize = metaData.getExpectedSize();

		Matrix input = new Matrix(inputSize, itemsRead, batchData.getInputBatch());
		Matrix expected = new Matrix(expectedSize, itemsRead, batchData.getExpectedBatch());
		
		BatchResult batchResult = engine.runForwards(input);
		
		if(trainingMode) {
			engine.runBackwards(batchResult, expected);
			
			synchronized (lock) {				
				engine.adjust(batchResult, learningRate);
			}
		}
		else {
			engine.evaluate(batchResult, expected);
		}
		
		return batchResult;

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	
		sb.append(String.format("Epochs: %d\n", epochs));
		sb.append(String.format("Initial learning rate: %.5f\n", initialLearningRate));
		sb.append(String.format("Final learning rate: %.5f\n", finalLearningRate));
		sb.append(String.format("Threads: %d\n", threads));
		
		sb.append("\nEngine Configuration");
		sb.append("\n--------------------\n");
		sb.append(engine);
	
		return sb.toString();
	}

	public boolean save(String file) {
		try(var ds = new ObjectOutputStream(new FileOutputStream(file))) {
			ds.writeObject(this);
		}
		catch(IOException e) {
			System.err.println("Unable to save to " + file);
			return false;
		}
		
		return true;
	}
	
	public static NeuralNetwork load(String file) {
		
		NeuralNetwork neuralNetwork = null;
		
		try(var ds = new ObjectInputStream(new FileInputStream(file))) {
			neuralNetwork = (NeuralNetwork)ds.readObject();
		}
		catch(Exception e) {
			System.err.println("Unable to load from " + file);
		}
		
		return neuralNetwork;
	}
	
	public Object readResolve() {
		this.lock = new Object();
		return this;
	}

}







