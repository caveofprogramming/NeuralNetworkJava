package cave;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.DoubleStream;

import org.junit.Test;

import cave.mnist.ImageLoader;
import cave.mnist.Loader;

public class TestLoader {

	private static double TOLERANCE = 0.00001;

	@Test
	public void testBatchLoading() throws Exception {

		var batchSize = 48;

		var directory = "../data/MNIST/";

		var imageFile = String.format("%s%s", directory, "train-images-idx3-ubyte");
		var labelFile = String.format("%s%s", directory, "train-labels-idx1-ubyte");

		Loader loader = new ImageLoader(imageFile, labelFile, batchSize);

		for (int epoch = 0; epoch < 20; epoch++) {

			var metaData = loader.open();
			var numberBatches = metaData.getNumberBatches();

			System.out.printf("Epoch %3d\n", epoch);

			var executor = Executors.newFixedThreadPool(12);

			List<Future<Integer>> list = new ArrayList<>();

			for (int i = 0; i < numberBatches; i++) {

				var future = executor.submit(() -> {

					var batchData = loader.readBatch();
					var inputBatch = batchData.getInputBatch();
					var expectedBatch = batchData.getExpectedBatch();
					var inputSize = metaData.getInputSize();
					var expectedSize = metaData.getExpectedSize();
					var nItems = metaData.getItemsRead();

					assertTrue(expectedBatch.length == nItems * expectedSize);
					assertTrue(inputBatch.length == nItems * inputSize);

					int index = 0;
					for (int itemIndex = 0; itemIndex < nItems; itemIndex++) {

						double sum = 0;

						for (int valueIndex = 0; valueIndex < inputSize; valueIndex++) {

							double value = inputBatch[index];

							sum += value;

							assertTrue(value >= 0 && value <= 1);

							index++;
						}
						
						assertTrue(sum > 15);
					}
					
					assertTrue(index == inputBatch.length);

					index = 0;
					for (int itemIndex = 0; itemIndex < nItems; itemIndex++) {
						
						double sum = 0.0;
						
						for (int valueIndex = 0; valueIndex < expectedSize; valueIndex++) {

							double value = expectedBatch[index];

							sum += value;

							assertTrue(Math.abs(value) < TOLERANCE || Math.abs(value - 1.0) < TOLERANCE);

							index++;
						}
						
						assertTrue(Math.abs(sum - 1.0) < TOLERANCE);
					}
					
					assertTrue(index == expectedBatch.length);

					return inputBatch.length;
				});

				list.add(future);
			}

			executor.shutdown();

			for (var future : list) {
				Integer value = future.get();
				assertTrue(value > 0);
			}

			loader.close();

		}

	}

}
