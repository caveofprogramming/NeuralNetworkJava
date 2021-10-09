package cave;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestEngine {

	private static double TOLERANCE = 0.001;

	@Test
	public void testApproximator() {

		Matrix input = Util.generateInputMatrix(5, 15).softmax();
		Matrix expected = Util.generateExpectedMatrix(5, 15);

		var approximatedGradient = Approximator.gradient(input, m -> {

			return m.apply((row, col, index, value) -> {
				return -expected.get(index) * Math.log(value);
			}).sumColumns();

			// return LossFunctions.categoricalCrossEntropy(input, expected);
		});

		Matrix expectedResult = input.apply((row, col, index, value) -> {
			return -expected.get(index) / value;
		});

		expectedResult.setTolerance(TOLERANCE);

		assertTrue(expectedResult.equals(approximatedGradient));
	}

	@Test
	public void testEngine() {

		Matrix input = Util.generateInputMatrix(8, 15);
		Matrix expected = Util.generateExpectedMatrix(3, 15);

		Engine engine = new Engine();
		engine.setBackpropToInput(true);
		
		int hiddenLayerSize = 10;
		int inputSize = 8;
		int outputSize = 3;
		
		engine.add(Transform.WEIGHTEDSUM, hiddenLayerSize, inputSize);
		engine.add(Transform.RELU);
		engine.add(Transform.DROPOUT, 0.5);
		engine.add(Transform.WEIGHTEDSUM, outputSize, hiddenLayerSize);
		engine.add(Transform.SOFTMAX);

		var approximatedGradient = Approximator.gradient(input, m -> {

			var batchResult = engine.runForwards(m);

			return LossFunctions.categoricalCrossEntropy(batchResult.getFinalOutput(), expected);
		});

		var batchResult = engine.runForwards(input);
		engine.runBackwards(batchResult, expected);

		approximatedGradient.setTolerance(TOLERANCE);

		var calculatedGradient = batchResult.getInitialError();

		assertTrue(approximatedGradient.equals(calculatedGradient));
	}

	@Test
	public void testengineAdjust() {

		var inputSize = 10;
		var outputSize = 5;
		var hiddenLayerSize = 20;

		Matrix input = Util.generateInputMatrix(inputSize, 15);
		Matrix expected = Util.generateExpectedMatrix(outputSize, 15);

		Engine engine = new Engine();
		
		
		engine.add(Transform.WEIGHTEDSUM, hiddenLayerSize, inputSize);
		engine.add(Transform.RELU);
		engine.add(Transform.DROPOUT, 0.5);
		engine.add(Transform.WEIGHTEDSUM, outputSize, hiddenLayerSize);
		engine.add(Transform.SOFTMAX);
		
		double totalLossDifference = 0;

		for (int i = 0; i < 10000; i++) {
			var batchResult1 = engine.runForwards(input);
			engine.runBackwards(batchResult1, expected);
			engine.adjust(batchResult1, 0.01);
			var batchResult2 = engine.runForwards(input);

			var loss1 = LossFunctions.categoricalCrossEntropy(batchResult1.getFinalOutput(), expected);
			var loss2 = LossFunctions.categoricalCrossEntropy(batchResult2.getFinalOutput(), expected);

			var averageLoss1 = loss1.sumColumns().get(0);
			var averageLoss2 = loss2.sumColumns().get(0);

			totalLossDifference += averageLoss1 - averageLoss2;
		}
		
		assertTrue(totalLossDifference > 0);

	}

}
