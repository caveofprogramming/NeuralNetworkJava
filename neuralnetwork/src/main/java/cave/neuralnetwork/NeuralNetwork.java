package cave.neuralnetwork;

public class NeuralNetwork {
	private Engine engine;
	
	public NeuralNetwork() {
		engine = new Engine();
	}
	
	public void add(Transform transform, double... params) {
		engine.add(transform, params);
	}

	@Override
	public String toString() {
		return engine.toString();
	}
	
	
}
